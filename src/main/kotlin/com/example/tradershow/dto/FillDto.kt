package com.example.tradershow.dto

import java.math.BigDecimal

data class FillDto(
    val price: String,
    val qty: String,
    val commission: String,
    val commissionAsset: String,
    val tradeId: Long
)
