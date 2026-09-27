package com.example.tradershow.repository

import com.example.tradershow.database.table.OrdersTable
import com.example.tradershow.dto.Allorders.AllOrdersTableResponseDto
import com.example.tradershow.dto.Allorders.TabdedalAllOrdersResponseDto
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Repository
class OrderRepository {
    fun save(
        symbol: String,
        side: String,
        type: String,
        quantity: BigDecimal,
        tabdealOrderId: Long,
        status: String,
        timestamp: Long
    ){
        transaction {
            OrdersTable.insert {
                it[OrdersTable.symbol] = symbol
                it[OrdersTable.type]= type
                it[OrdersTable.quantity] = quantity
                it[OrdersTable.tabdealOrderId] = tabdealOrderId
                it[OrdersTable.status] = status
                it[OrdersTable.side] = side
                it[OrdersTable.timestamp] = timestamp
            }
        }
    }

    fun getAllOrders():List<AllOrdersTableResponseDto>
    {
        val ordersList : MutableList<AllOrdersTableResponseDto> = mutableListOf()
        transaction {
            OrdersTable.selectAll().forEach { order ->
                ordersList.add(
                    AllOrdersTableResponseDto(
                        order[OrdersTable.symbol],
                        order[OrdersTable.side],
                        order[OrdersTable.type],
                        order[OrdersTable.quantity],
                        order[OrdersTable.tabdealOrderId],
                        order[OrdersTable.status],
                        Instant.ofEpochMilli(order[OrdersTable.timestamp])
                            .atZone(ZoneId.of("Asia/Tehran"))
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    )
                )
            }
        }
        return ordersList
    }
}