package com.example.tradershow.dto

data class CoinPriceResponseDto(
    val symbol: String,
    val base:String,
    val quote:String,
    val price: String,
    val time: Long,
)

