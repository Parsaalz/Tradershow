package com.example.tradershow.service

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.database.table.StopLossState
import com.example.tradershow.dto.CoinPriceResponseDto
import com.example.tradershow.dto.CoinPriceUserResponseDto
import com.example.tradershow.dto.ExchangeInfoResponseDto
import com.example.tradershow.exception.MarketNotFoundException
import com.example.tradershow.exception.TabdealApiException
import com.example.tradershow.repository.AlertSystemRepository
import com.example.tradershow.repository.CoinPricesRepository
import com.example.tradershow.repository.StopLossRepository
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.lang.System
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
class CoinPriceService(
    private val symbolNormalizer: SymbolNormalizerService,
    private val tabdealClient: TabdealClient,
    private val symbolAliesService: SymbolAliasService,
    private val cacheManager: CacheManager,
    private val coinRepo: CoinPricesRepository
) {
    fun getCoinPrice(symbol: String): CoinPriceUserResponseDto {
        val normalizedSymbol = symbolNormalizer.normalize(symbol)

        val checkedAliesTable: String = symbolAliesService.searchAlias(normalizedSymbol)

        val cachedValue =
            cacheManager.getCache("exchange-info")?.get("exchangeInfo")?.get()

        val resultExchangeInfo =
            (cachedValue as? List<ExchangeInfoResponseDto>)
                ?.takeIf { it.isNotEmpty() }
                ?: tabdealClient.getExchangeInfo()

        val usedSymbol =
            resultExchangeInfo.find { query -> query.status == "TRADING" && (query.symbol == checkedAliesTable || query.baseAsset == checkedAliesTable) }?.symbol
                ?: throw MarketNotFoundException("بازاری فعالی یافت نشد")


        val result = coinRepo.findBySymbol(usedSymbol) ?: throw TabdealApiException("قیمتی برای این ارز پیدا نشد")
        return result
    }

}


@Component
class GetExchangeInfoSchedule(
    private val tabdealClient: TabdealClient, private val cacheManager: CacheManager
) {
    @Scheduled(fixedRate = 60 * 60 * 1000)
    fun getExchangeInfo() {
        try {
            val result = tabdealClient.getExchangeInfo()

            cacheManager.getCache("exchange-info")?.put("exchangeInfo", result)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}

//1678203
@Component
class GetTradesSchedule(
    private val tabdealClient: TabdealClient,
    private val cacheManager: CacheManager,
    private val coinRepo: CoinPricesRepository,
    private val logger: Logger,
) {
    @Scheduled(fixedDelay = 1_000)
    fun getTrades() {
        logger.info("start get coins price", GetTradesSchedule::class.java)
        try {
            val executer = Executors.newFixedThreadPool(10)
            val exchangeInfo =
                cacheManager.getCache("exchange-info")?.get("exchangeInfo")?.get() as List<ExchangeInfoResponseDto>

            val symbols = exchangeInfo

            symbols.chunked(15).forEach { batch ->
                val futures = batch.map { task ->
                    executer.submit {
                        val result = tabdealClient.getTrades(task.symbol).toCoinPriceResponseDto(
                            base = task.baseAsset,
                            quote = task.quoteAsset,
                            symbol = task.symbol,
                            time = System.currentTimeMillis()
                        )
                        transaction {
                            coinRepo.save(
                                task.symbol,
                                price = result.price.toBigDecimal(),
                                time = result.time,
                            )
                        }
                    }
                }

                futures.forEach { it.get() }

                Thread.sleep(5_000)
            }


        } catch (e: Exception) {
            println(e.message)
        }
        logger.info("end get coins price", GetTradesSchedule::class.java)
    }
}


@Component
class GetSomeCoinsPricesSchedule(
    private val tabdealClient: TabdealClient,
    private val cacheManager: CacheManager,
    private val coinRepo: CoinPricesRepository,
    private val logger: Logger,
    private val priorityCoinServiceHandler: PriorityCoinServiceHandler
) {
    @Scheduled(fixedDelay = 1_000)
    fun getImportantCoinsPrices() {
        logger.info("start get important coins price", GetSomeCoinsPricesSchedule::class.java)
        try {
            val executer = Executors.newFixedThreadPool(10)
            var symbols = priorityCoinServiceHandler.priorityCoins.keys.toList()

            symbols.chunked(15).forEach { batch ->
                val futures = batch.map { task ->
                    executer.submit {
                        val result = tabdealClient.getTrades(task).toCoinPriceResponseDto(
                            base = task,
                            quote = task,
                            symbol = task,
                            time = System.currentTimeMillis()
                        )
                        transaction {
                            coinRepo.save(
                                task,
                                price = result.price.toBigDecimal(),
                                time = result.time,
                            )
                        }
                    }
                }

                futures.forEach { it.get() }

                Thread.sleep(2_000)
            }


        } catch (e: Exception) {
            println(e.message)
        }
        logger.info("end get important coins price", GetSomeCoinsPricesSchedule::class.java)
    }
}


@Component
class PriorityCoinServiceHandler(
    private val alertSystemRepository: AlertSystemRepository,
    private val logger: Logger,
    private val stopLossRepository: StopLossRepository
) {
    val priorityCoins = ConcurrentHashMap<String, AtomicInteger>()
    @EventListener(ApplicationReadyEvent::class)
    fun fillPriorityCoins() {
        val result = alertSystemRepository.getByActive()
        val result2=stopLossRepository.findByState(StopLossState.PENDING)
        result.forEach { alertSystem ->
            priorityCoins.computeIfAbsent(alertSystem.symbol) { AtomicInteger(0) }.incrementAndGet()
        }
        result2.forEach { alertSystem ->
            priorityCoins.computeIfAbsent(alertSystem.symbol) { AtomicInteger(0) }.incrementAndGet()
        }
        logger.warn("-----------------$priorityCoins-----------------", PriorityCoinServiceHandler::class.java)

    }

}