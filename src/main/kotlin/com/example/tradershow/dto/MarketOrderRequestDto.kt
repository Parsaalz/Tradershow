package com.example.tradershow.dto

import java.math.BigDecimal

data class MarketOrderRequestDto(
    val symbol: String,
    val side: Side,
    val quantity: Double,
    val type: Type,
    val price: BigDecimal?,
    val stopPrice: BigDecimal?,
)



