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

@Service
class AlertSystemService(
    private val alertRepo: AlertSystemRepository,
) {
    fun createAlert(requestDto: CreateAlertRequestDto): CreateAlertResponseDto
    {
        alertRepo.save(
            requestDto.symbol,
            requestDto.targetPrice,
            requestDto.direction,
            requestDto.phoneNumber
        )
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
)
{
    @Scheduled(fixedDelay = 10_000)
    fun checkAlertsAndSendSms()
    {
        logger.log("--------------- Alerts Checking Scheduler started! ------------------", AlertService::class.java)
        val result = alertRepo.getByActive()
        logger.log(result.toString(), AlertService::class.java)
        result.forEach { alert ->
            val coinPrice = coinPriceService.getCoinPrice(alert.symbol)

            if (alert.direction == DirectionType.ABOVE && coinPrice.price > alert.targetPrice) {
                sendSmsAndFinalizeAlert(alert)
            } else if (alert.direction == DirectionType.BELLOW && coinPrice.price < alert.targetPrice) {
                sendSmsAndFinalizeAlert(alert)
            }
        }
        logger.log("--------------- Alerts Checking Scheduler finished! ------------------", AlertService::class.java)
    }

    fun sendSmsAndFinalizeAlert(alert: AlertTableResponseDto) {
        smsDotIrClient.sendTriggeredSms(alert.phoneNumber,"هشداری که گذاشته بودید روی ${alert.symbol} رسیده است ")
        alertRepo.updateActiveFalseById(alert.id)
    }
}