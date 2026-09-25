package com.example.tradershow.config

import com.example.tradershow.database.table.CoinPrices
import com.example.tradershow.database.table.OrdersTable
import jakarta.annotation.PostConstruct
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.context.annotation.Configuration


@Configuration
class DatabaseConfig {

    @PostConstruct
    fun connect()
    {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/tradershow",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres",
        )
        transaction {
            SchemaUtils.create(OrdersTable)
            SchemaUtils.create(CoinPrices)
        }
    }
}