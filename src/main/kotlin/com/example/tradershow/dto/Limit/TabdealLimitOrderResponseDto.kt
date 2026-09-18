package com.example.tradershow.dto.Limit

import com.example.tradershow.dto.FillDto

data class TabdealLimitOrderResponseDto(
    val symbol: String,
    val orderId: Long,
    val transactTime: Long,
    val origQty: String,
    val executedQty: String,
    val cummulativeQuoteQty: String,
    val status: String,
    val type: String,
    val side: String,
    val price: String,
)
{
    fun toLimitOrderUserResponseDto():LimitOrderUserResponseDto{
        return LimitOrderUserResponseDto(
            symbol = symbol,
            orderId = orderId,
            side = side,
            price = price,
            status = status,
            type = type,
            quantity = cummulativeQuoteQty,
            executedQuantity = executedQty,
            total = cummulativeQuoteQty,
        )
    }
}