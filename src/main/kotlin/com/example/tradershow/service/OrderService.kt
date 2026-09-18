package com.example.tradershow.service

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.dto.Conditional.ConditionalOrderRequestDto
import com.example.tradershow.dto.Conditional.ConditionalOrderUserResponseDto
import com.example.tradershow.dto.Limit.LimitOrderRequestDto
import com.example.tradershow.dto.Limit.LimitOrderUserResponseDto
import com.example.tradershow.dto.LotSize
import com.example.tradershow.dto.Market.MarketOrderRequestDto
import com.example.tradershow.dto.Market.MarketOrderUserResponseDto
import com.example.tradershow.dto.Market.SubmitMarketOrderRequestDto
import com.example.tradershow.dto.MinNotional
import com.example.tradershow.exception.MarketNotFoundException
import com.example.tradershow.repository.OrderRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import kotlin.let
import kotlin.text.toBigDecimal
import kotlin.toBigDecimal

@Service
class OrderService(
    private val normalizerService: SymbolNormalizerService,
    private val symbolAliasService: SymbolAliasService,
    private val tabdealClient: TabdealClient,
    private val orderRepository: OrderRepository
) {
    fun submitMarketOrder(requestDto: MarketOrderRequestDto): MarketOrderUserResponseDto {
        requestDto.validate()

        val normalizedSymbol = normalizerService.normalize(requestDto.symbol)


        val alizedSymbol = symbolAliasService.searchAlias(normalizedSymbol)


        val markets = tabdealClient.getExchangeInfo()
        val existSymbol = markets.find { query ->
            query.quoteAsset == "IRT" && query.status == "TRADING" &&
                    (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: markets.find { query ->
            query.quoteAsset == "USDT" && query.status == "TRADING" &&
                    (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: throw MarketNotFoundException("بازاری پیدا نشد")


        if (
            requestDto.quantity.toBigDecimal() <
            (
                    markets.find { it.symbol == existSymbol }
                        ?.filters
                        ?.filterIsInstance<LotSize>()
                        ?.firstOrNull()
                        ?.minQty
                        ?.toBigDecimal()
                        ?: BigDecimal.ZERO
                    )
        ) {
            throw MarketNotFoundException("مقدار ورودی مقدار کمتر از حد پایین نمی باشد برای خرید لطفا در خرید خود توجه کنید ")
        }
        val newRequestDto = SubmitMarketOrderRequestDto(
            symbol = existSymbol,
            side = requestDto.side,
            type = requestDto.type,
            quantity = requestDto.quantity.toBigDecimal(),
            timestamp = System.currentTimeMillis(),
        )

        // TODO: create better conversion between DTOs
        val result = tabdealClient.submitMarketOrder(newRequestDto)
        val userResponse = result?.toMarketOrderUserResponseDto() ?: throw RuntimeException()
        orderRepository.save(
            userResponse.symbol,
            userResponse.side,
            userResponse.type,
            userResponse.quantity.toBigDecimal(),
            userResponse.orderId,
            userResponse.status
        )
        return userResponse
    }

    fun submitLimitOrder(requestDto: LimitOrderRequestDto): LimitOrderUserResponseDto
    {
        val normalizedSymbol = normalizerService.normalize(requestDto.symbol)
        val alizedSymbol = symbolAliasService.searchAlias(normalizedSymbol)
        val markets = tabdealClient.getExchangeInfo()
        val existSymbol = markets.find { query ->
            query.quoteAsset == "IRT" && query.status == "TRADING" &&
                    (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: markets.find { query ->
            query.quoteAsset == "USDT" && query.status == "TRADING" &&
                    (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: throw MarketNotFoundException("بازاری پیدا نشد")
        if (
            requestDto.quantity <
            (
                    markets.find { it.symbol == existSymbol }
                        ?.filters
                        ?.filterIsInstance<LotSize>()
                        ?.firstOrNull()
                        ?.minQty
                        ?.toBigDecimal()
                        ?: BigDecimal.ZERO
                    )
        ) {
            throw MarketNotFoundException("مقدار ورودی مقدار کمتر از حد پایین نمی باشد برای خرید لطفا در خرید خود توجه کنید ")
        }
        val newRequestDto = requestDto.toSubmitLimitOrderRequestDto(timestamp = System.currentTimeMillis(),existSymbol)
        val result = tabdealClient.submitLimitOrder(newRequestDto)
        orderRepository.save(
            result.symbol,
            result.side,
            result.type,
            result.cummulativeQuoteQty.toBigDecimal(),
            result.orderId,
            result.status
        )
        return result.toLimitOrderUserResponseDto()
    }


    fun submitConditionalOrder(requestDto: ConditionalOrderRequestDto): ConditionalOrderUserResponseDto
    {
        val normalizedSymbol = normalizerService.normalize(requestDto.symbol)
        val alizedSymbol = symbolAliasService.searchAlias(normalizedSymbol)
        val markets = tabdealClient.getExchangeInfo()
        val existSymbol = markets.find { query ->
            query.quoteAsset == "IRT" && query.status == "TRADING" &&
                    (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: markets.find { query ->
            query.quoteAsset == "USDT" && query.status == "TRADING" &&
                    (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: throw MarketNotFoundException("بازاری پیدا نشد")
        if (
            requestDto.quantity.toBigDecimal() <
            (
                    markets.find { it.symbol == existSymbol }
                        ?.filters
                        ?.filterIsInstance<LotSize>()
                        ?.firstOrNull()
                        ?.minQty
                        ?.toBigDecimal()
                        ?: BigDecimal.ZERO
                    )
        ) {
            throw MarketNotFoundException("مقدار ورودی مقدار کمتر از حد پایین نمی باشد برای خرید لطفا در خرید خود توجه کنید ")
        }

        val newRequestDto = requestDto.toSubmitConditionalRequestDto(
            timestamp = System.currentTimeMillis(),
            symbol = existSymbol,
        )
        val result = tabdealClient.submitConditionalOrder(newRequestDto).toConditionalOrderUserResponseDto()
        orderRepository.save(
            result.symbol,
            result.side,
            result.type,
            result.cummulativeQuoteQty.toBigDecimal(),
            result.orderId,
            result.status,
        )
        return result

    }
}