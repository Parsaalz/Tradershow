package com.example.tradershow.dto.Market

import java.math.BigDecimal

data class SubmitMarketOrderRequestDto(
    val symbol: String,
    val side: Side,
    val type: Type,
    val quantity: BigDecimal,
    val timestamp: Long,
)
{
    fun toTabdealSubmitMarketOrderRequestDto(signature: String): TabdealSubmitMarketOrderRequestDto
    {
        return TabdealSubmitMarketOrderRequestDto(
            symbol,
            side,
            type,
            quantity,
            timestamp,
            signature = signature,
        )
    }
}

enum class Side{
    BUY,
    SELL
}
enum class Type{
    MARKET,
    LIMIT,
    STOP_LOSS_LIMIT,
}


