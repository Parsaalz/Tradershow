package com.example.tradershow.dto

import java.math.BigDecimal

data class MarketResponseDto(
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
    val fills: List<FillDto>
)
{
    fun toMarketOrderUserResponseDto(quantity : BigDecimal): MarketOrderUserResponseDto {
        return MarketOrderUserResponseDto(
            symbol = symbol,
            orderId = orderId,
            side = side,
            type = type,
            status = status,
            quantity = quantity,
            executedQuantity = executedQty,
            price = price,
            total = cummulativeQuoteQty,
        )
    }
}
