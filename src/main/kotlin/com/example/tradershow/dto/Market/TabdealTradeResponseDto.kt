package com.example.tradershow.dto.Market

import com.example.tradershow.dto.CoinPriceResponseDto
import com.example.tradershow.dto.Limit.TabdealLimitOrderResponseDto

data class TabdealTradeResponseDto(
    val bids: List<List<String>>,
) {
    fun toCoinPriceResponseDto(base: String, quote: String, symbol: String,time:Long): CoinPriceResponseDto {
        return CoinPriceResponseDto(
            price = bids.first().first(),
            time = time,
            base = base,
            quote = quote,
            symbol = symbol,
        )
    }
}
