package com.example.tradershow.service

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.dto.CoinPriceResponseDto
import com.example.tradershow.dto.CoinPriceUserResponseDto
import com.example.tradershow.dto.ExchangeInfoResponseDto
import com.example.tradershow.exception.MarketNotFoundException
import com.example.tradershow.exception.TabdealApiException
import com.example.tradershow.repository.CoinPricesRepository
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.concurrent.Executors

@Service
@Cacheable("price_info")
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

        var resultExchangeInfo = cacheManager.getCache("exchange-info")?.get("exchangeInfo")?.get() as List<ExchangeInfoResponseDto>

        if(checkedAliesTable.isNullOrEmpty()){
            resultExchangeInfo = tabdealClient.getExchangeInfo()
        }

        val usedSymbol: String =
            resultExchangeInfo.find { query -> query.quoteAsset == "USDT" && query.status == "TRADING" && (query.symbol == checkedAliesTable || query.baseAsset == checkedAliesTable) }?.symbol
                ?: throw MarketNotFoundException("بازاری یافت نشد")


        val result = coinRepo.findBySymbol(usedSymbol)?:throw TabdealApiException("قیمتی برای این کوین پیدا نشد")
        return result
    }

}


@Component
class GetExchangeInfoSchedule(
    private val tabdealClient: TabdealClient,
    private val cacheManager: CacheManager
) {
    @Scheduled(fixedRate = 60_000)
    fun getExchangeInfo() {
        try {
            val result = tabdealClient.getExchangeInfo()

            cacheManager.getCache("exchange-info")
                ?.put("exchangeInfo", result)
        }catch (e: Exception) {
            println(e.message)
        }
    }
}

@Component
class GetTradesSchedule(
    private val tabdealClient: TabdealClient,
    private val cacheManager: CacheManager,
    private val coinRepo: CoinPricesRepository
)
{
    @Scheduled(fixedDelay = 10_000)
    fun getTrades() {
        try {
            val executer = Executors.newFixedThreadPool(10)
            val exchangeInfo = cacheManager.getCache("exchange-info")?.get("exchangeInfo")?.get() as List<ExchangeInfoResponseDto>

            val symbols = exchangeInfo?.map { it.symbol } as List<String>

            symbols.chunked(15)
                .forEach { batch ->
                    val futures = batch.forEach { task ->
                        executer.submit {
                            val result = tabdealClient.getTrades(task).first().toCoinPriceResponseDto(
                                base = task,
                                quote = "USDT",
                                symbol = task,
                            )
                            coinRepo.save(
                                task,
                                price = result.price.toBigDecimal(),
                                time = result.time,
                            )
                        }
                    }
                    Thread.sleep(5_000)
                }
            executer.shutdown()


        }catch (e: Exception)
        {
            println(e.message)
        }
    }
}