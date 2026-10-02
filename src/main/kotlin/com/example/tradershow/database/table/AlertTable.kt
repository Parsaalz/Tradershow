package com.example.tradershow.database.table

import com.example.tradershow.dto.AlertSystem.DirectionType
import org.jetbrains.exposed.sql.Table

object AlertTable: Table("AlertTable") {
    val id = long("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val symbol = varchar("symbol", 255)
    val targetPrice = decimal("targetPrice",36,18)
    val direction = enumerationByName<DirectionType>("direction", 12)
    val phoneNumber = varchar("phoneNumber",15)
    val active = bool("active").default(true)
    val triggeredAt = long("triggeredAt").nullable()
    val createdAt = long("createdAt")
}