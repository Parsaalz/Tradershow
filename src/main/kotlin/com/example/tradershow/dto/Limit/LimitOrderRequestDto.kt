package com.example.tradershow.dto.Limit

import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import java.math.BigDecimal

data class LimitOrderRequestDto(
    val symbol: String,
    val side: Side,
    val quantity: BigDecimal,
    val type: Type,
    val price: BigDecimal,
)
{
    fun toSubmitLimitOrderRequestDto(timestamp: Long,existSymbol:String): SubmitLimitOrderRequestDto {
        return SubmitLimitOrderRequestDto(
            symbol = existSymbol,
            side = side,
            quantity = quantity,
            type = type,
            price = price,
            timestamp = timestamp,
        )
    }
}
