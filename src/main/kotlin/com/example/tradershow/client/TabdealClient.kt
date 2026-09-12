package com.example.tradershow.client

import com.example.tradershow.dto.ExchangeInfoResponseDto
import com.example.tradershow.dto.MarketResponseDto
import com.example.tradershow.dto.SubmitOrderRequestDto
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.tradershow.dto.TabdealTradeResponseDto
import com.example.tradershow.exception.MarketNotFoundException
import com.example.tradershow.exception.TabdealApiException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import tools.jackson.databind.ObjectMapper
import tools.jackson.core.type.TypeReference
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class TabdealClient {

    private val client = OkHttpClient()
    private val objectMapper = ObjectMapper()
    private val apiKey = System.getenv("TABDEAL_API_KEY")
    private val apiSecret = System.getenv("TABDEAL_API_SECRET")

    fun getTrades(symbol: String): List<TabdealTradeResponseDto> {

        val request = Request.Builder()
            .url(
                "https://api1.tabdeal.org/r/api/v1/trades?symbol=$symbol&limit=1"
            )
            .get()
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")
        }

        val body = response.body?.string() ?: throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")
        if (body.isEmpty()) throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")
        val trades = objectMapper.readValue(
            body,
            object : TypeReference<List<TabdealTradeResponseDto>>() {}
        )

        return trades
    }


    @Cacheable("exchangeInfo")
    fun getExchangeInfo(): List<ExchangeInfoResponseDto> {
        println("🔥 CALLING TABDEAL EXCHANGE INFO")
        val request = Request.Builder()
            .url("https://api1.tabdeal.org/r/api/v1/exchangeInfo/")
            .get()
            .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")
        }
        val body = response.body?.string() ?: throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")
        if (body.isEmpty()) throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")
        val exchangeInfo = objectMapper.readValue(
            body,
            object : TypeReference<List<ExchangeInfoResponseDto>>() {}
        )
        return exchangeInfo
    }

    fun submitOrderInTabdeal(requestDto: SubmitOrderRequestDto): MarketResponseDto? {
        val json = objectMapper.writeValueAsString(requestDto)
        val body = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://api1.tabdeal.org/api/v1/order")
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {

        }
        val responseBody = response.body?.string() ?: throw TabdealApiException("بامشکلی روبرو شدیم")
        val result = objectMapper.readValue(
            responseBody,
            object : TypeReference<MarketResponseDto>() {}
        )

        return result


    }
}