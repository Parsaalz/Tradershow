package com.example.tradershow.dto.StopLoss

import com.example.tradershow.database.table.StopLossState
import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import java.math.BigDecimal

data class StopLossOrderUserRequest(
    val symbol: String,
    val stopLossPrice: BigDecimal,
    val price:BigDecimal,
    val type: Type,
    val side: Side,
    val quantity: BigDecimal,
)
{
    fun toStopLossUserResponse(timestamp: Long): StopLossUserResponseDto {
        return StopLossUserResponseDto(
            this.symbol,
            this.stopLossPrice,
            this.price,
            stopLossPrice,
            timestamp,
            quantity = quantity,
            result = StopLossState.PENDING,
            side,
            type,
        )
    }
}
