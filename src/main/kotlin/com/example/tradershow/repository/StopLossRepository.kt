package com.example.tradershow.repository

import com.example.tradershow.database.table.StopLossLimitOrdersTable
import com.example.tradershow.database.table.StopLossState
import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import com.example.tradershow.dto.StopLoss.StopLossOrderTableResponse
import com.example.tradershow.dto.StopLoss.StopLossUserResponseDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class StopLossRepository {

    fun save(
        symbol: String,
        quantity: BigDecimal,
        stopPrice: BigDecimal,
        price: BigDecimal,
        timestamp: Long,
        side: Side,
        type : Type,
        currentPrice: BigDecimal,
        result:StopLossState,
        )
    {
        transaction {
            StopLossLimitOrdersTable.insert {
                it[StopLossLimitOrdersTable.symbol] = symbol
                it[StopLossLimitOrdersTable.quantity] = quantity
                it[StopLossLimitOrdersTable.stopPrice] = stopPrice
                it[StopLossLimitOrdersTable.price] = price
                it[StopLossLimitOrdersTable.timestamp] = timestamp
                it[StopLossLimitOrdersTable.side] = side
                it[StopLossLimitOrdersTable.type] = type
                it[StopLossLimitOrdersTable.currentPrice] = currentPrice
                it[StopLossLimitOrdersTable.result]=result
            }
        }
    }


    fun findByState(state: StopLossState): List<StopLossOrderTableResponse>
    {
        return transaction {
            StopLossLimitOrdersTable.selectAll().where(StopLossLimitOrdersTable.result.eq(state)).map {
                StopLossOrderTableResponse(
                    it[StopLossLimitOrdersTable.id],
                    it[StopLossLimitOrdersTable.symbol],
                    it[StopLossLimitOrdersTable.stopPrice],
                    it[StopLossLimitOrdersTable.stopPrice],
                    it[StopLossLimitOrdersTable.price],
                    it[StopLossLimitOrdersTable.timestamp],
                    it[StopLossLimitOrdersTable.quantity],
                    it[StopLossLimitOrdersTable.result],
                    it[StopLossLimitOrdersTable.side],
                    it[StopLossLimitOrdersTable.type],
                    it[StopLossLimitOrdersTable.currentPrice],
                )
            }.toList()
        }
    }


    fun updateState(id: Long,state:StopLossState)
    {
        transaction {
            StopLossLimitOrdersTable.update(
                where = { StopLossLimitOrdersTable.id eq id },
                body = {
                    it[StopLossLimitOrdersTable.result] = state
                }
            )
        }
    }


}