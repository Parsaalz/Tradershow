package com.example.tradershow.service

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.dto.CoinPriceResponseDto
import com.example.tradershow.dto.ExchangeInfoResponseDto
import com.example.tradershow.exception.MarketNotFoundException
import com.example.tradershow.exception.TabdealApiException
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping

@Service
class CoinPriceService(
    private val symbolNormalizer: SymbolNormalizerService,
    private val tabdealClient: TabdealClient,
    private val symbolAliesService: SymbolAliasService
) {
    fun getCoinPrice(symbol: String): CoinPriceResponseDto {
        val normalizedSymbol = symbolNormalizer.normalize(symbol)

        val checkedAliesTable: String = symbolAliesService.searchAlias(normalizedSymbol)


        val resultExchangeInfo = tabdealClient.getExchangeInfo()
        val usedSymbol: String =
            resultExchangeInfo.find { query -> query.quoteAsset == "USDT" && query.status == "TRADING" && (query.symbol == checkedAliesTable || query.baseAsset == checkedAliesTable) }?.symbol
                ?: throw MarketNotFoundException("بازاری یافت نشد")


        val result = tabdealClient.getTrades(usedSymbol)
        if (result.isEmpty()) throw TabdealApiException("قیمتی برای این کوین پیدا نشد")
        return result.first().toCoinPriceResponseDto(usedSymbol, "USDT", symbol = usedSymbol)
    }

}