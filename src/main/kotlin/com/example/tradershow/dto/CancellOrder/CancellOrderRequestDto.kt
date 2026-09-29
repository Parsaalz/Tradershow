package com.example.tradershow.dto.CancellOrder

data class CancellOrderRequestDto(
    val symbol: String,
    val orderId: String,
)
