package com.example.tradershow.service

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.dto.Allorders.AllOrdersRequestUserResponse
import com.example.tradershow.dto.Allorders.AllOrdersTableResponseDto
import com.example.tradershow.repository.OrderRepository
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
class AllOrdersService(
    private val tabdealClient: TabdealClient,
    private val orderRepo: OrderRepository,
) {
    fun getAllOrders():List<AllOrdersTableResponseDto>{
        val result = orderRepo.getAllOrders()
        return result
    }


    fun getOrder(orderId: String): AllOrdersTableResponseDto{
        val id = orderId.toLong()
        return orderRepo.getOrderById(id)
    }
}



