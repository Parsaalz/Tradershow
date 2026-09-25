package com.example.tradershow.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class CacheConfig() {

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager()
        cacheManager.registerCustomCache(
            "exchange-info",
            Caffeine.newBuilder()
                .build<Any,Any>()
        )
        cacheManager.registerCustomCache(
            "price-info",
            Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(10))
            .build<Any,Any>()
        )
        return cacheManager

    }
}