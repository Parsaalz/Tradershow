package com.example.tradershow.client

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class SmsDotIrClient(
    private val objectMapper: ObjectMapper,
) {

    private val client = OkHttpClient()

    private val apiUrl = "https://api.sms.ir/v1/send/bulk"
    private val apiKey = "hGiv0KcAyR4b40Ac97QUMiXJs6U4tN5KRzIh4Oc2vNWLUka5"
    private val lineNumber = 30002128097073L

    fun sendTriggeredSms(
        phoneNumber: String,
        message: String,
    ) {
        val requestDto = SmsRequestDto(
            lineNumber = lineNumber,
            messageText = message,
            mobiles = listOf(phoneNumber),
            sendDateTime = null,
        )

        val json = objectMapper.writeValueAsString(requestDto)

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("X-API-KEY", apiKey)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .post(
                json.toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )
            )
            .build()

        client.newCall(request).execute().use { response ->

            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                throw IllegalStateException(
                    "SMS request failed. status=${response.code}, body=$body"
                )
            }

            println("SMS sent successfully: $body")
        }
    }
}

data class SmsRequestDto(
    val lineNumber: Long,
    val messageText: String,
    val mobiles: List<String>,
    val sendDateTime: Long?,
)