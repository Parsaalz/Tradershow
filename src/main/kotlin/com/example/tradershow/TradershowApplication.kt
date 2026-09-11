package com.example.tradershow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class TradershowApplication

fun main(args: Array<String>) {
	runApplication<TradershowApplication>(*args)
}
