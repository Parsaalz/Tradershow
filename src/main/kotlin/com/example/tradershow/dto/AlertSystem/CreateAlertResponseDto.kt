package com.example.tradershow.dto.AlertSystem

import java.math.BigDecimal

data class CreateAlertResponseDto(
    val symbol: String,
    val targetPrice: BigDecimal,
    val phoneNumber: String,
    val direction: DirectionType,
    val status:String = "با موفقیت ساخته شد"
)
