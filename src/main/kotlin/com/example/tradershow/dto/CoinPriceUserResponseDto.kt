package com.example.tradershow.dto

data class CoinPriceUserResponseDto(
    val symbol: String,
    val base: String,
    val quote: String,
    val price: Double,
    val time: Long,
)
