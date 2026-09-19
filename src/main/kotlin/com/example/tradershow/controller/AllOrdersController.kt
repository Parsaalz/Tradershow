package com.example.tradershow.controller

import com.example.tradershow.dto.Allorders.AllOrdersRequestUserResponse
import com.example.tradershow.service.AllOrdersService
import com.example.tradershow.service.OrderService
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/orders")
class AllOrdersController (
    val allOrdersService: AllOrdersService
){

    @GetMapping()
    fun getOrders(): List<AllOrdersRequestUserResponse> {
        return allOrdersService.getAllOrders()
    }
}