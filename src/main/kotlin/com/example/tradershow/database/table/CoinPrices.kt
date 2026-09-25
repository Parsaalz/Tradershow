package com.example.tradershow.database.table

import org.jetbrains.exposed.sql.Table

object CoinPrices: Table("coin_prices") {
    val symbol = varchar("symbol", 50).uniqueIndex()
    val price = double("price")
    val lastUpdatedTime = long("last_updated_time")
}