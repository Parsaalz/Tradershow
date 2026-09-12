package com.example.tradershow.dto

import java.math.BigDecimal

data class SubmitOrderRequestDto(
    val symbol: String,
    val side: Side,
    val type: Type,
    val quantity: BigDecimal,
    val timestamp: Long,
    val price: BigDecimal?,
    val stopPrice: BigDecimal?,
)

enum class Side{
    SELL,
    BUY
}
enum class Type {
    MARKET,
    LIMIT,
    STOP_LOSS_LIMIT
}