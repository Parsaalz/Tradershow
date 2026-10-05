package com.example.tradershow.service

import com.example.tradershow.client.SmsDotIrClient
import com.example.tradershow.database.table.AlertTable
import com.example.tradershow.database.table.AlertTable.direction
import com.example.tradershow.dto.AlertSystem.AlertTableResponseDto
import com.example.tradershow.dto.AlertSystem.CreateAlertRequestDto
import com.example.tradershow.dto.AlertSystem.CreateAlertResponseDto
import com.example.tradershow.dto.AlertSystem.DirectionType
import com.example.tradershow.repository.AlertSystemRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@Service
class AlertSystemService(
    private val alertRepo: AlertSystemRepository,
    private val priorityCoinHandler:PriorityCoinServiceHandler
) {
    fun createAlert(requestDto: CreateAlertRequestDto): CreateAlertResponseDto
    {
        alertRepo.save(
            requestDto.symbol,
            requestDto.targetPrice,
            requestDto.direction,
            requestDto.phoneNumber
        )
        priorityCoinHandler.priorityCoins.computeIfAbsent(requestDto.symbol){ AtomicInteger(0) }.incrementAndGet()
        return CreateAlertResponseDto(
            requestDto.symbol,
            requestDto.targetPrice,
            requestDto.phoneNumber,
            requestDto.direction,
        )
    }
}


@Component
class AlertService(
    private val alertRepo: AlertSystemRepository,
    private val coinPriceService: CoinPriceService,
    private val smsDotIrClient: SmsDotIrClient,
    private val logger: Logger,
    private val priorityCoinHandler:PriorityCoinServiceHandler,
)
{
    @Scheduled(fixedDelay = 10_000)
    fun checkAlertsAndSendSms()
    {
        println(priorityCoinHandler.priorityCoins)
        logger.info("Alerts Checking Scheduler started!", AlertService::class.java)
        val result = alertRepo.getByActive()
        logger.info(result.toString(), AlertService::class.java)
        result.forEach { alert ->
            val coinPrice = coinPriceService.getCoinPrice(alert.symbol)

            if (alert.direction == DirectionType.ABOVE && coinPrice.price > alert.targetPrice) {
                sendSmsAndFinalizeAlert(alert)
                priorityCoinHandler.priorityCoins.computeIfPresent(alert.symbol) { _, count ->
                    if (count.decrementAndGet() <= 0) null else count
                }

            } else if (alert.direction == DirectionType.BELLOW && coinPrice.price < alert.targetPrice) {
                sendSmsAndFinalizeAlert(alert)
                priorityCoinHandler.priorityCoins.computeIfPresent(alert.symbol) { _, count ->
                    if (count.decrementAndGet() <= 0) null else count
                }
            }
        }
        logger.info("Alerts Checking Scheduler finished!", AlertService::class.java)
    }

    fun sendSmsAndFinalizeAlert(alert: AlertTableResponseDto) {
        smsDotIrClient.sendTriggeredSms(alert.phoneNumber,"هشداری که گذاشته بودید روی ${alert.symbol} رسیده است ")
        alertRepo.updateActiveFalseById(alert.id)
    }
}