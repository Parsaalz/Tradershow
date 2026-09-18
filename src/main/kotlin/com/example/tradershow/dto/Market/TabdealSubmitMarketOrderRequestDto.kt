package com.example.tradershow.dto.Market

import java.math.BigDecimal

data class TabdealSubmitMarketOrderRequestDto(
    val symbol: String,
    val side: Side,
    val type: Type,
    val quantity: BigDecimal,
    val timestamp: Long,
    val signature: String,
)