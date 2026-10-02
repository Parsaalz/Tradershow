package com.example.tradershow.dto.CancellOrder

data class CancelStopLossOrderResponseDto(
    val orderId: Long,
    val message:String="با موفقیت کنسل شد "
)
