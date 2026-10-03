package com.example.tradershow.service

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface Logger {
    fun <T> log(message: String, clazz: Class<T>)
}

@Service
@Primary
class LoggerService: Logger {
    override fun <T> log(message: String, clazz: Class<T>) {
        println("${LocalDateTime.now()}-${clazz.simpleName}: $message")
    }
}