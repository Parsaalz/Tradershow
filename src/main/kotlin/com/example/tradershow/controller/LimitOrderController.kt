package com.example.tradershow.controller

import com.example.tradershow.dto.Limit.LimitOrderRequestDto
import com.example.tradershow.dto.Limit.LimitOrderUserResponseDto
import com.example.tradershow.service.OrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/orders/spot")
class LimitOrderController(
    val orderService: OrderService
) {
    @PostMapping
    fun submitLimitOrder(@RequestBody requestDto: LimitOrderRequestDto): LimitOrderUserResponseDto
    {
        return orderService.submitLimitOrder(requestDto)
    }
}