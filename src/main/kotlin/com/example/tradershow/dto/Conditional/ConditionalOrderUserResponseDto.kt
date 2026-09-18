package com.example.tradershow.dto.Conditional

data class ConditionalOrderUserResponseDto(
    val symbol: String,
    val orderId: Long,
    val transactTime: Long,
    val origQty: String,
    val executedQty: String,
    val cummulativeQuoteQty: String,
    val status: String,
    val type: String,
    val side: String,
    val price: String,
    val stopPrice: String,
)
