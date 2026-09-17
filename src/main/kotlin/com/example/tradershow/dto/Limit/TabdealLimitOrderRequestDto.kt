package com.example.tradershow.dto.Limit

import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import java.math.BigDecimal

data class TabdealLimitOrderRequestDto(
    val timestamp: Long,
    val symbol: String,
    val price: BigDecimal,
    val quantity: BigDecimal,
    val type: Type,
    val side: Side,
    val signature:String,
    )
