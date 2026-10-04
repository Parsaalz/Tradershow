package com.example.tradershow.service

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface Logger {
    fun<T>info(message: String,clazz: Class<T>)
    fun<T>error(message: String,clazz: Class<T>)
    fun<T>warn(message: String,clazz: Class<T>)
    fun<T>debug(message: String,clazz: Class<T>)
}

@Service
@Primary
class LoggerService: Logger {
    override fun <T> info(message: String, clazz: Class<T>) {
        LoggerFactory.getLogger(clazz).info(message)
    }
    override fun <T> error(message: String, clazz: Class<T>) {
        LoggerFactory.getLogger(clazz).error(message)
    }
    override fun <T> warn(message: String, clazz: Class<T>) {
        LoggerFactory.getLogger(clazz).warn(message)
    }
    override fun <T> debug(message: String, clazz: Class<T>) {
        LoggerFactory.getLogger(clazz).debug(message)
    }
}