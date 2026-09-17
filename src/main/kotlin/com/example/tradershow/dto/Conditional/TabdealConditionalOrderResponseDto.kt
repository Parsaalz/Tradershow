package com.example.tradershow.dto.Conditional

data class TabdealConditionalOrderResponseDto(
    val symbol: String,
    val orderId: Long,
    val transactTime: Long,
    val origQty: String,
    val executedQty: String,
    val cummulativeQuoteQty: String,
    val status: String,
    val type: String,
    val side: String,
    val price: String,
    val stopPrice: String,
)
{
    fun toConditionalOrderUserResponseDto(): ConditionalOrderUserResponseDto {
        return ConditionalOrderUserResponseDto(
            side = side,
            price = price,
            status = status,
            type = type,
            stopPrice= stopPrice,
            cummulativeQuoteQty = cummulativeQuoteQty,
            symbol = symbol,
            orderId = orderId,
            transactTime = transactTime,
            origQty = origQty,
            executedQty = executedQty,
        )
    }
}
