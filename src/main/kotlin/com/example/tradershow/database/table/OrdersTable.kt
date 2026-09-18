package com.example.tradershow.database.table

import org.jetbrains.exposed.sql.Table

object OrdersTable : Table("orders") {
    val id = long("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val symbol = varchar("symbol", 50)
    val side = varchar("side", 10)
    val type = varchar("type", 30)
    val quantity = decimal("quantity", 30, 15)
    val tabdealOrderId = long("tabdealOrderId")
    val status = varchar("status", 100)
}