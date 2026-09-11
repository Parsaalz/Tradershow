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
        val caffeine = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
        val cacheManager = CaffeineCacheManager()
        cacheManager.setCaffeine(caffeine)
        return cacheManager

    }
}