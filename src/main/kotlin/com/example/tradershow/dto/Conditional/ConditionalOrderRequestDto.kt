package com.example.tradershow.dto.Conditional

import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import java.math.BigDecimal

data class ConditionalOrderRequestDto(
    val symbol: String,
    val side: Side,
    val quantity: Double,
    val type: Type,
    val stopPrice: BigDecimal,
    val price: BigDecimal,
)
{
    fun toSubmitConditionalRequestDto(timestamp: Long,symbol: String): SubmitConditionalOrderRequestDto
    {
        return SubmitConditionalOrderRequestDto(
            symbol = symbol,
            this.side,
            this.type,
            this.quantity.toBigDecimal(),
            timestamp,
            this.stopPrice,
            this.price,
        )
    }
}
