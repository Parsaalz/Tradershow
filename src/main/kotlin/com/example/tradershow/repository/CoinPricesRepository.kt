package com.example.tradershow.repository

import com.example.tradershow.database.table.CoinPrices
import com.example.tradershow.dto.CoinPriceUserResponseDto
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository


@Repository
class CoinPricesRepository {

    fun save(
        symbol: String,
        price: Double,
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
            CoinPrices.selectAll().where { CoinPrices.symbol eq  symbol }.map {
                CoinPriceUserResponseDto(
                    it[CoinPrices.symbol],
                    it[CoinPrices.symbol],
                    "USDT",
                    it[CoinPrices.price],
                    it[CoinPrices.lastUpdatedTime]
                )
            }.singleOrNull()
        }
    }
}