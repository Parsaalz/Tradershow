package com.example.tradershow.dto.CancellOrder

data class TabdealCancellOrderDtoResponse(
    val symbol:String?,
    val tabdealSymbol:String?,
    val timestamp:Long?,
    val orderId:String,
    val status:String?,
    val type:String?,
    val side:String?,
    val isWorking:Boolean?,
    val isStopOrderTriggered:Boolean?,
)
