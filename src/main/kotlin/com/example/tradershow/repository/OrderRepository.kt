package com.example.tradershow.repository

import com.example.tradershow.database.table.OrdersTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class OrderRepository {
    fun save(
        symbol: String,
        side: String,
        type: String,
        quantity: BigDecimal,
        tabdealOrderId: Long,
        status: String
    ){
        transaction {
            OrdersTable.insert {
                it[OrdersTable.symbol] = symbol
                it[OrdersTable.type]= type
                it[OrdersTable.quantity] = quantity
                it[OrdersTable.tabdealOrderId] = tabdealOrderId
                it[OrdersTable.status] = status
                it[OrdersTable.side] = side
            }
        }
    }
}