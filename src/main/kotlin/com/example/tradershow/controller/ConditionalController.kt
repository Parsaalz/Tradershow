package com.example.tradershow.controller

import com.example.tradershow.dto.Conditional.ConditionalOrderRequestDto
import com.example.tradershow.dto.Conditional.ConditionalOrderUserResponseDto
import com.example.tradershow.service.OrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/orders/conditional")
class ConditionalController(
    val orderService: OrderService,
) {

    @PostMapping
    fun submitConditionalOrder(@RequestBody requestDto: ConditionalOrderRequestDto):ConditionalOrderUserResponseDto
    {
        return orderService.submitConditionalOrder(requestDto)
    }
}