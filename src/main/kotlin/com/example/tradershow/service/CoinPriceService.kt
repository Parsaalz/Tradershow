package com.example.tradershow.service

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.dto.CoinPriceResponseDto
import com.example.tradershow.dto.ExchangeInfoResponseDto
import com.example.tradershow.exception.MarketNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping

@Service
class CoinPriceService(
    private val symbolNormalizer: SymbolNormalizerService,
    private val tabdealClient: TabdealClient,
    private val symbolAliesService: SymbolAliasService
) {
    fun getCoinPrice(symbol: String): CoinPriceResponseDto {
        // TODO: normalize symbol before retrieving data from tabdeal API
        val normalizedSymbol = symbolNormalizer.normalize(symbol)

        val checkedAliesTable:String= symbolAliesService.searchAlias(normalizedSymbol)
        println(checkedAliesTable)


        val resultExchangeInfo = tabdealClient.getExchangeInfo()
        val usedSymbol: String =
            resultExchangeInfo.find { query -> query.quoteAsset == "USDT" && query.status == "TRADING" && (query.symbol == checkedAliesTable || query.baseAsset == checkedAliesTable) }?.symbol?:throw MarketNotFoundException("بازار یافت نشد")


        val result = tabdealClient.getTrades(usedSymbol)
        return result.last().toCoinPriceResponseDto(usedSymbol, "USDT", symbol = usedSymbol)
    }

}