package com.example.tradershow.dto.StopLoss

import com.example.tradershow.database.table.StopLossState
import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import java.math.BigDecimal

data class StopLossOrderTableResponse(
    val id: Long,
    val symbol: String,
    val stopLossPrice: BigDecimal,
    val price: BigDecimal,
    val stopPrice: BigDecimal,
    val timestamp: Long,
    val quantity: BigDecimal,
    val result: StopLossState,
    val side : Side,
    val type : Type,
    val currentPrice : BigDecimal,
)
