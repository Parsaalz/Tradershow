package com.example.tradershow.controller

import com.example.tradershow.dto.MarketOrderRequestDto
import com.example.tradershow.dto.MarketOrderUserResponseDto
import com.example.tradershow.service.OrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/orders")
class MarketOrderController (
    private val orderService: OrderService
){
    @PostMapping
    fun getOrder(@RequestBody requestDto: MarketOrderRequestDto): MarketOrderUserResponseDto
    {
        val result=orderService.submitOrder(requestDto)
        return result
    }






}