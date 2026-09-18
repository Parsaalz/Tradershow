package com.example.tradershow.dto.Limit

import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import java.math.BigDecimal

data class SubmitLimitOrderRequestDto(
    val symbol: String,
    val side: Side,
    val type: Type,
    val quantity: BigDecimal,
    val timestamp: Long,
    val price: BigDecimal,
)
{
    fun toTabdealLimitOrderRequestDto(signature:String): TabdealLimitOrderRequestDto {
        return TabdealLimitOrderRequestDto(
            symbol = symbol,
            side = side,
            type = type,
            timestamp = timestamp,
            price = price,
            signature = signature,
            quantity = quantity
        )
    }
}
