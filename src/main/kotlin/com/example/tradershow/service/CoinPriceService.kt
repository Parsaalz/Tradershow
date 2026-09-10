package com.example.tradershow.service

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.dto.CoinPriceResponseDto
import com.example.tradershow.dto.ExchangeInfoResponseDto
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping

@Service
class CoinPriceService(
    private val tabdealClient: TabdealClient
) {
    fun getCoinPrice(symbol: String): CoinPriceResponseDto {
        // TODO: normalize symbol before retrieving data from tabdeal API
        val resultExchangeInfo=tabdealClient.getExchangeInfo()
        val usedSymbol:String=resultExchangeInfo.find { query -> query.quoteAsset == "USDT" && query.baseAsset == symbol && query.status == "TRADING"}?.symbol?:"BTCUSDT"





        val result = tabdealClient.getTrades(usedSymbol)
        return result.last().toCoinPriceResponseDto(usedSymbol, "USDT",symbol=usedSymbol)
    }

//    GET https://api1.tabdeal.org/r/api/v1/exchangeInfo [NONE]
}