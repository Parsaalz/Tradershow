package com.example.tradershow.controller

import com.example.tradershow.dto.CancellOrder.CancelStopLossOrderResponseDto
import com.example.tradershow.dto.CancellOrder.CancellOrderRequestDto
import com.example.tradershow.service.OrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/cancel/stop-loss-orders")
class CancelStopLossOrderController(
    private val orderService: OrderService,
) {
    @PostMapping
    fun cancelStopLoss(
        @RequestBody requestDto: CancellOrderRequestDto
    ): CancelStopLossOrderResponseDto
    {
        return orderService.cancelStopLossLimitOrder(requestDto)
    }
}