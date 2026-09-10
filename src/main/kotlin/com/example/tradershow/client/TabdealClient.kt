package com.example.tradershow.client
import com.example.tradershow.dto.ExchangeInfoResponseDto
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.tradershow.dto.TabdealTradeResponseDto
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import tools.jackson.core.type.TypeReference

@Component
class TabdealClient {

    private val client = OkHttpClient()
    private val objectMapper = ObjectMapper()

    fun getTrades(symbol: String): List<TabdealTradeResponseDto> {

        // TODO: read Builder design pattern
        val request = Request.Builder()
            .url(
                "https://api1.tabdeal.org/r/api/v1/trades?symbol=$symbol&limit=1"
            )
            .get()
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw RuntimeException("Tabdeal request failed")
        }

        val body = response.body?.string() ?: throw RuntimeException("Tabdeal request failed")
        val trades = objectMapper.readValue(
            body,
            object : TypeReference<List<TabdealTradeResponseDto>>() {}
        )

        return trades
    }
    fun getExchangeInfo(): List<ExchangeInfoResponseDto>
    {
        val request = Request.Builder()
            .url("https://api1.tabdeal.org/r/api/v1/exchangeInfo/")
            .get()
            .build()
        val response= client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw RuntimeException("Tabdeal request failed")
        }
        val body = response.body?.string() ?: throw RuntimeException("Tabdeal request failed")
        val exchangeInfo = objectMapper.readValue(
            body,
            object : TypeReference<List<ExchangeInfoResponseDto>>() {}
        )
        return exchangeInfo
    }
}