package com.example.tradershow.dto

import java.math.BigDecimal

data class CoinPriceUserResponseDto(
    val symbol: String,
    val base: String,
    val quote: String,
    val price: BigDecimal,
    val lastUpdatedTime: String,
)
