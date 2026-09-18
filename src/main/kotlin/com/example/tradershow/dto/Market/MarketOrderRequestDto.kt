package com.example.tradershow.dto.Market

import com.example.tradershow.exception.QuantityNotTrueException
import java.math.BigDecimal

data class MarketOrderRequestDto(
    val symbol: String,
    val side: Side,
    val quantity: Double,
    val type: Type,
) {
    fun validate() {
        if (this.quantity <= 0) throw QuantityNotTrueException("مقدار خرید یا فروش باید بیشتر از صفر باشد")
    }
}



