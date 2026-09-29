package com.example.tradershow.dto.Allorders

import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import java.math.BigDecimal

data class AllOrdersTableResponseDto(
    val symbol: String,
    val orderId : String,
    val side: String,
    val type: String,
    val quantity: BigDecimal,
    val tabdealOrderId: Long,
    val status: String,
    val orderTime: String,
)
