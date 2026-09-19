package com.example.tradershow.dto.Allorders

import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.Type
import java.math.BigDecimal

data class AllOrdersRequestUserResponse(
    val symbol: String,
    val tabdealSymbol: String,
    val orderId: BigDecimal,
    val transactTime: Long,
    val price: String,
    val origQty: String,
    val executedQty :String,
    val status: String,
    val type : Type,
    val side: Side,
    val stopPrice: String,
    val updateTime: String,
    val isWorking: Boolean,
    val isStopOrderTriggered: Boolean,
)
