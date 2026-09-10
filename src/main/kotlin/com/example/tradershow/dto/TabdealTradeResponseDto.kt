package com.example.tradershow.dto

import com.sun.tools.javac.tree.TreeInfo.symbol
import java.time.LocalDateTime

data class TabdealTradeResponseDto(
    val price: String,
    val time: Long,
) {
    fun toCoinPriceResponseDto(base: String, quote: String, symbol: String): CoinPriceResponseDto {
        return CoinPriceResponseDto(
            price = price,
            time = time,
            base = base,
            quote = quote,
            symbol = symbol,
        )
    }
}
