package com.example.tradershow.dto

data class ExchangeInfoResponseDto(
    val symbol: String,
    val tabdealSymbol:String,
    val status:String,
    val baseAsset:String,
    val quoteAsset:String,
)

