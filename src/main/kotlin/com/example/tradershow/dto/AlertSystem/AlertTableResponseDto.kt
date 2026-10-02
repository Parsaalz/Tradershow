package com.example.tradershow.dto.AlertSystem

import com.example.tradershow.database.table.AlertTable
import java.math.BigDecimal

data class AlertTableResponseDto(
    val id:Long,
    val symbol: String,
    val targetPrice: BigDecimal,
    val direction: DirectionType,
    val phoneNumber: String,
)
