package com.example.tradershow.dto

import java.math.BigDecimal

data class MarketOrderUserResponseDto(
    val orderId: Long,
    val symbol: String,
    val side: String,
    val type: String,
    val quantity: BigDecimal,
    val executedQuantity: String,
    val price: String,
    val total: String,
    val status: String,
)