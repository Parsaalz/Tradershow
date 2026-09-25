package com.example.tradershow.repository

import com.example.tradershow.database.table.CoinPrices
import com.example.tradershow.dto.CoinPriceUserResponseDto
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun Long.toTehranTime():String{
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneId.of("Asia/Tehran"))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}

@Repository
class CoinPricesRepository {

    fun save(
        symbol: String,
        price: BigDecimal,
        time: Long,
    ) {
        transaction {
            CoinPrices.upsert(CoinPrices.symbol) {
                it[CoinPrices.symbol] = symbol
                it[CoinPrices.price] = price
                it[CoinPrices.lastUpdatedTime] = time
            }
        }
    }


    fun findBySymbol(symbol: String): CoinPriceUserResponseDto? {
        return transaction {
            CoinPrices.selectAll().where { CoinPrices.symbol eq symbol }.map {
                CoinPriceUserResponseDto(
                    it[CoinPrices.symbol],
                    it[CoinPrices.symbol],
                    "USDT",
                    it[CoinPrices.price],
                    it[CoinPrices.lastUpdatedTime].toTehranTime()
                )
            }.single()
        }
    }
}