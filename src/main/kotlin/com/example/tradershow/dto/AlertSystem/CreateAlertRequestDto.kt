package com.example.tradershow.dto.AlertSystem

import java.math.BigDecimal

data class CreateAlertRequestDto(
    val symbol: String,
    val targetPrice: BigDecimal,
    val direction: DirectionType,
    val phoneNumber: String,
)


enum class DirectionType {
    BELLOW,
    ABOVE
}
