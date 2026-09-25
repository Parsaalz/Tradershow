package com.example.tradershow.database.table

import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import org.jetbrains.exposed.sql.Table

object StopLossLimitOrdersTable: Table("StopLossLimitOrders") {
    val id = long("id")
    override val primaryKey = PrimaryKey(id)
    val symbol = varchar("symbol", 100)
    val timestamp = long("timestamp")
    val side = enumerationByName<Side>("side",50)
    val quantity = decimal("quantity",30,18)
    val stopPrice = decimal("stop_price",30,18)
    val price = decimal("price",30,18)
    val type = enumerationByName<Type>("type",50)
    val result = enumerationByName<StopLossState>("resultState",20)
    val currentPrice = decimal("current_price",30,18)
}




enum class StopLossState{
    PENDING,
    PROCESSING,
    TRIGGERED,
    FAILED,
    CANCELLED,
    SUCCESS
}