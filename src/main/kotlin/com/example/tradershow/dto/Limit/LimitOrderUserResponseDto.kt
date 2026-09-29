package com.example.tradershow.dto.Limit

import com.example.tradershow.database.table.OrderStatus

data class LimitOrderUserResponseDto(
    val orderId: Long,
    val symbol: String,
    val side: String,
    val type: String,
    val quantity: String,
    val executedQuantity: String,
    val price: String,
    val total: String,
    val status: String,
)
