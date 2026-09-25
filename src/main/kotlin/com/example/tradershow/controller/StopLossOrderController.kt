package com.example.tradershow.controller

import com.example.tradershow.dto.StopLoss.StopLossOrderUserRequest
import com.example.tradershow.dto.StopLoss.StopLossUserResponseDto
import com.example.tradershow.service.OrderService
import jakarta.annotation.PostConstruct
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/orders/stoploss")
class StopLossOrderController(
    val orderService: OrderService,
) {

    @PostMapping
    fun submitOrder(@RequestBody requestDto: StopLossOrderUserRequest): StopLossUserResponseDto
    {
        return orderService.submitStopLossLimitOrderService(requestDto)
    }

}