package com.example.tradershow.dto.Market

data class MarketOrderUserResponseDto(
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