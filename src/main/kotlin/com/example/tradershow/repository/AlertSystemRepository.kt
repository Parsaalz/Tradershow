package com.example.tradershow.repository

import com.example.tradershow.database.table.AlertTable
import com.example.tradershow.dto.AlertSystem.AlertTableResponseDto
import com.example.tradershow.dto.AlertSystem.DirectionType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.math.BigDecimal
@Repository
class AlertSystemRepository(

) {

    fun save(
        symbol: String,
        targetPrice: BigDecimal,
        direction: DirectionType,
        phoneNumber: String,
    )
    {
        transaction {
            AlertTable.insert {
                it[AlertTable.symbol] = symbol
                it[AlertTable.targetPrice] = targetPrice
                it[AlertTable.direction] = direction
                it[AlertTable.phoneNumber] = phoneNumber
                it[AlertTable.createdAt] = System.currentTimeMillis()
            }
        }
    }

    fun getByActive(): List<AlertTableResponseDto>
    {
        return transaction {
            AlertTable.selectAll().where { AlertTable.active eq true }.map { alert ->
                AlertTableResponseDto(
                    symbol = alert[AlertTable.symbol],
                    targetPrice = alert[AlertTable.targetPrice],
                    id = alert[AlertTable.id],
                    direction = alert[AlertTable.direction],
                    phoneNumber = alert[AlertTable.phoneNumber],
                )
            }
        }
    }

    fun updateActiveFalseById(
        id: Long
    )
    {
        transaction {
            AlertTable.update({ AlertTable.id eq id }) {
                it[AlertTable.active] = false
                it[AlertTable.triggeredAt] = System.currentTimeMillis()
            }
        }
    }
}