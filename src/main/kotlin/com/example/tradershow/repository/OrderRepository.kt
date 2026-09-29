package com.example.tradershow.repository

import com.example.tradershow.database.table.OrdersTable
import com.example.tradershow.database.table.StopLossLimitOrdersTable.result
import com.example.tradershow.dto.Allorders.AllOrdersTableResponseDto
import com.example.tradershow.dto.Allorders.TabdedalAllOrdersResponseDto
import com.example.tradershow.util.SnowflakeIdGenerator
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Repository
class OrderRepository(
    private val snowFlakeIdGenerator: SnowflakeIdGenerator,
) {
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
                it[OrdersTable.orderId]=snowFlakeIdGenerator.nextId()
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
                        order[OrdersTable.orderId].toString(),
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

    fun getOrderById(orderId: Long): AllOrdersTableResponseDto {
        return transaction {
            val result = OrdersTable.selectAll().where { (OrdersTable.orderId eq orderId) or (OrdersTable.tabdealOrderId eq orderId) }.first()
             AllOrdersTableResponseDto(
                result[OrdersTable.symbol],
                 result[OrdersTable.orderId].toString(),
                result[OrdersTable.side],
                result[OrdersTable.type],
                result[OrdersTable.quantity],
                result[OrdersTable.tabdealOrderId],
                result[OrdersTable.status],
                result[OrdersTable.timestamp].toTehranTime()
            )
        }
    }


    fun updateOrderById(
        orderId: Long,
        side: String?=null,
        type: String?=null,
        quantity: BigDecimal?=null,
        status: String?=null,
        timestamp: Long?=null,
    ) {
        transaction {
            OrdersTable.update(
                where = { OrdersTable.orderId eq orderId }
            ) {
                if (side != null) {
                    it[OrdersTable.side] = side
                }

                if (type != null) {
                    it[OrdersTable.type] = type
                }

                if (quantity != null) {
                    it[OrdersTable.quantity] = quantity
                }

                if (status != null) {
                    it[OrdersTable.status] = status
                }

                if (timestamp != null) {
                    it[OrdersTable.timestamp] = timestamp
                }
            }
        }
    }
}