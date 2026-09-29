package com.example.tradershow.controller

import com.example.tradershow.dto.Allorders.AllOrdersRequestUserResponse
import com.example.tradershow.dto.Allorders.AllOrdersTableResponseDto
import com.example.tradershow.repository.OrderRepository
import com.example.tradershow.service.AllOrdersService
import com.example.tradershow.service.OrderService
import org.springframework.core.annotation.Order
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/orders")
class AllOrdersController (
    val allOrdersService: AllOrdersService,
    val orderRepo: OrderRepository,
){

    @GetMapping()
    fun getOrders(): List<AllOrdersTableResponseDto> {
        return allOrdersService.getAllOrders()
    }

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id :String ): AllOrdersTableResponseDto {
        return allOrdersService.getOrder(id)
    }
}