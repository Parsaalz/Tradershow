package com.example.tradershow.dto

import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type

data class TabdealOrderStatusResponseDto(
    val orderId: Long,
    val status: String,
    val isWorking: Boolean,
    val isStopOrderTriggered:Boolean,
)
