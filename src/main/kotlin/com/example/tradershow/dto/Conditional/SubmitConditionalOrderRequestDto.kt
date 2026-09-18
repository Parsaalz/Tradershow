package com.example.tradershow.dto.Conditional

import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import java.math.BigDecimal

data class SubmitConditionalOrderRequestDto(
    val symbol: String,
    val side: Side,
    val type: Type,
    val quantity: BigDecimal,
    val timestamp: Long,
    val stopPrice: BigDecimal,
    val price: BigDecimal,
)
