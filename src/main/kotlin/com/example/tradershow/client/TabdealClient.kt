package com.example.tradershow.client

import com.example.tradershow.dto.Allorders.TabdedalAllOrdersResponseDto
import com.example.tradershow.dto.Conditional.SubmitConditionalOrderRequestDto
import com.example.tradershow.dto.Conditional.TabdealConditionalOrderResponseDto
import com.example.tradershow.dto.ExchangeInfoResponseDto
import com.example.tradershow.dto.Market.MarketOrderResponseDto
import com.example.tradershow.dto.Limit.SubmitLimitOrderRequestDto
import com.example.tradershow.dto.Limit.TabdealLimitOrderResponseDto
import com.example.tradershow.dto.Market.SubmitMarketOrderRequestDto
import com.example.tradershow.dto.Market.TabdealTradeResponseDto
import com.example.tradershow.exception.TabdealApiException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString.Companion.encodeUtf8
import okio.HashingSource.Companion.hmacSha256
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper
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
    private val formUrlEncoded = "application/x-www-form-urlencoded".toMediaType()

    private fun generateSignature(
        queryString: String,
        secret: String
    ): String {
        val secretKey = SecretKeySpec(
            secret.toByteArray(StandardCharsets.UTF_8),
            "HmacSHA256"
        )
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKey)
        val hash = mac.doFinal(queryString.toByteArray(StandardCharsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun urlEncode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8)

    private fun toQueryString(params: Map<String, String>): String =
        params.entries.joinToString("&") { (key, value) ->
            "${urlEncode(key)}=${urlEncode(value)}"
        }

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

    fun submitMarketOrder(requestDto: SubmitMarketOrderRequestDto): MarketOrderResponseDto? {
        val key = apiKey ?: throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")
        val secret = apiSecret ?: throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")

        val params = linkedMapOf(
            "side" to requestDto.side.name,
            "type" to requestDto.type.name,
            "quantity" to requestDto.quantity.toPlainString(),
            "symbol" to requestDto.symbol,
            "timestamp" to System.currentTimeMillis().toString(),
        )
        val queryString = toQueryString(params)
        val signature = generateSignature(queryString, secret)
        val body = "$queryString&signature=$signature".toRequestBody(formUrlEncoded)

        val request = Request.Builder()
            .url("https://api1.tabdeal.org/api/v1/order")
            .addHeader("X-MBX-APIKEY", key)
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw TabdealApiException("بامشکلی روبرو شدیم")
        println("ORDER STATUS: ${response.code}")
        println("ORDER RESPONSE: $responseBody")
        if (!response.isSuccessful) {
            throw TabdealApiException("بامشکلی روبرو شدیم")
        }
        return objectMapper.readValue(
            responseBody,
            object : TypeReference<MarketOrderResponseDto>() {}
        )
    }


    fun submitLimitOrder(requestDto: SubmitLimitOrderRequestDto): TabdealLimitOrderResponseDto{
        val key = apiKey ?: throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")
        val secret = apiSecret ?: throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")

        val params = linkedMapOf(
            "side" to requestDto.side.name,
            "type" to requestDto.type.name,
            "quantity" to requestDto.quantity.toPlainString(),
            "price" to (requestDto.price.toPlainString() ?: "0"),
            "symbol" to requestDto.symbol,
            "timestamp" to System.currentTimeMillis().toString(),
        )
        val queryString = toQueryString(params)
        val signature = generateSignature(queryString, secret)
        val body = "$queryString&signature=$signature".toRequestBody(formUrlEncoded)
        val request = Request.Builder()
        .url("https://api1.tabdeal.org/api/v1/order")
        .addHeader("X-MBX-APIKEY", key)
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw TabdealApiException("بامشکلی روبرو شدیم")
        println("ORDER STATUS: ${response.code}")
        println("ORDER RESPONSE: $responseBody")
        if (!response.isSuccessful) {
            throw TabdealApiException("\"بامشکلی روبرو شدیم\"")
        }
        return objectMapper.readValue(
            responseBody,
            object : TypeReference<TabdealLimitOrderResponseDto>() {}
        )
    }


    fun submitConditionalOrder(requestDto: SubmitConditionalOrderRequestDto):TabdealConditionalOrderResponseDto{
        val key = apiKey ?: throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")
        val secret = apiSecret ?: throw TabdealApiException("با خطایی هنگام اتصال مواجه شدیم")

        val params = linkedMapOf(
            "side" to requestDto.side.name,
            "type" to requestDto.type.name,
            "quantity" to requestDto.quantity.toPlainString(),
            "stopPrice" to (requestDto.stopPrice.toPlainString()),
            "price" to (requestDto.price.toPlainString()),
            "symbol" to requestDto.symbol,
            "timestamp" to System.currentTimeMillis().toString(),
        )
        val queryString = toQueryString(params)
        val signature = generateSignature(queryString, secret)
        val body = "$queryString&signature=$signature".toRequestBody(formUrlEncoded)
        val request = Request.Builder()
            .url("https://api1.tabdeal.org/api/v1/order")
            .addHeader("X-MBX-APIKEY", key)
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw TabdealApiException("\"بامشکلی روبرو شدیم\"")
        println("ORDER STATUS: ${response.code}")
        println("ORDER RESPONSE: $responseBody")
        if (!response.isSuccessful) {
            throw TabdealApiException("\"بامشکلی روبرو شدیم\"")
        }
        return objectMapper.readValue(
            responseBody,
            object : TypeReference<TabdealConditionalOrderResponseDto>() {}
        )
    }

    fun getAllOrders(): List<TabdedalAllOrdersResponseDto> {

        val timestamp = System.currentTimeMillis()

        val query:String = "limit=50&timestamp=$timestamp"

        val signature  = generateSignature(query,apiSecret)

        val url =
            "https://api1.tabdeal.org/r/api/v1/allOrders?$query&signature=$signature"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-MBX-APIKEY", apiKey)
            .build()

        val response = client.newCall(request).execute()

        val responseBody = response.body?.string()

        println("ORDER STATUS: ${response.code}")
        println("ORDER RESPONSE: $responseBody")

        if (!response.isSuccessful) {
            throw TabdealApiException("سرویس در دسترس نمیباشد")
        }

        return objectMapper.readValue(
            responseBody,
            object : TypeReference<List<TabdedalAllOrdersResponseDto>>() {}
        )
    }
}