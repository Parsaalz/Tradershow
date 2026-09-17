package com.example.tradershow.dto.Market

import com.example.tradershow.dto.FillDto

data class  MarketOrderResponseDto(
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
    val fills: List<FillDto>? = null,
)
{
    fun toMarketOrderUserResponseDto(): MarketOrderUserResponseDto {
        return MarketOrderUserResponseDto(
            symbol = symbol,
            orderId = orderId,
            side = side,
            type = type,
            status = status,
            quantity = origQty,
            executedQuantity = executedQty,
            price = price,
            total = cummulativeQuoteQty,
        )
    }
}
