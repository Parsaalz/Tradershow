package com.example.tradershow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.logging.Level
import java.util.logging.Logger
import okhttp3.OkHttpClient


@EnableCaching
@SpringBootApplication
class TradershowApplication

fun main(args: Array<String>) {
	runApplication<TradershowApplication>(*args)
	Logger.getLogger(OkHttpClient::class.java.name).level = Level.FINE
}
