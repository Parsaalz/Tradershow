package com.example.tradershow.controller

import com.example.tradershow.dto.CancellOrder.CancellOrderRequestDto
import com.example.tradershow.dto.CancellOrder.TabdealCancellOrderDtoResponse
import com.example.tradershow.service.OrderService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/cancel-order")
class CancellOrderController (
    private val orderService: OrderService
){

    @PostMapping
    fun cancellOrder(
        @RequestBody requestDto: CancellOrderRequestDto
    ): TabdealCancellOrderDtoResponse
    {
        return orderService.cancellOrder(requestDto)
    }
}