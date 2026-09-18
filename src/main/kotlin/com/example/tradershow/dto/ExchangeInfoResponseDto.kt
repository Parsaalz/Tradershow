package com.example.tradershow.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.math.BigDecimal

data class ExchangeInfoResponseDto(
    val symbol: String,
    val tabdealSymbol: String,
    val status: String,
    val baseAsset: String,
    val quoteAsset: String,
    val filters: List<Filters>,
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "filterType"
)
@JsonSubTypes(
    JsonSubTypes.Type(
        value = MinNotional::class,
        name = "MIN_NOTIONAL"
    ),
    JsonSubTypes.Type(
        value = LotSize::class,
        name = "LOT_SIZE"
    ),
    JsonSubTypes.Type(
        value = PriceFilter::class,
        name = "PRICE_FILTER"
    ),
    JsonSubTypes.Type(
        value = PercentPrice::class,
        name = "PERCENT_PRICE"
    ),
    JsonSubTypes.Type(
        value = MarketLotSize::class,
        name = "MARKET_LOT_SIZE"
    )
)
sealed interface Filters

data class MinNotional(
    val minNotional: String,
    val applyToMarket: Boolean,
    val avgPriceMins: Int
) : Filters

data class LotSize(
    val minQty: String,
    val stepSize: String,
) : Filters

data class PriceFilter(
    val minPrice: String?,
    val maxPrice: String?,
    val tickSize: String,
) : Filters

data class PercentPrice(
    val multiplierUp: BigDecimal,
    val multiplierDown: BigDecimal,
    val avgPriceMins: Int,
) : Filters

data class MarketLotSize(
    val minQty: String,
    val maxQty: String,
    val stepSize: String,
) : Filters