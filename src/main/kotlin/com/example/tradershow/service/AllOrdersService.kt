package com.example.tradershow.service

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.dto.Allorders.AllOrdersRequestUserResponse
import com.example.tradershow.dto.Allorders.AllOrdersTableResponseDto
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
class AllOrdersService(
    private val tabdealClient: TabdealClient,
) {
    fun getAllOrders():List<AllOrdersTableResponseDto>{
        val result=tabdealClient.getAllOrders()
//        return result.map {
//            order -> order.toAllOrderRequestUserResponseDto()
//        }
        return result
    }
}