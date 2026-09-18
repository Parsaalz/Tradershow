package com.example.tradershow.dto.Market

import com.example.tradershow.dto.CoinPriceResponseDto

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
