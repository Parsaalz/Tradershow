package com.example.tradershow.database.table

import org.jetbrains.exposed.sql.Table

object CoinPrices: Table("coin_prices") {
    val symbol = varchar("symbol", 50).uniqueIndex()
    val price = decimal("price",30,18)
    val lastUpdatedTime = long("last_updated_time")
}