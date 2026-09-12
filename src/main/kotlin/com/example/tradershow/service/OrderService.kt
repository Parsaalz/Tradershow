package com.example.tradershow.service

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.dto.MarketOrderRequestDto
import com.example.tradershow.dto.MarketOrderUserResponseDto
import com.example.tradershow.dto.Side
import com.example.tradershow.dto.SubmitOrderRequestDto
import com.example.tradershow.dto.Type
import com.example.tradershow.exception.MarketNotFoundException
import com.example.tradershow.exception.QuantityNotTrueException
import com.example.tradershow.exception.SideWrongException
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val normalizerService: SymbolNormalizerService,
    private val symbolAliasService: SymbolAliasService,
    private val tabdealClient: TabdealClient
) {
    fun submitOrder(requestDto: MarketOrderRequestDto): MarketOrderUserResponseDto {
        val normalizedSymbol = normalizerService.normalize(requestDto.symbol)


        val alizedSymbol = symbolAliasService.searchAlias(normalizedSymbol)


        val existSymbol = tabdealClient.getExchangeInfo()
            .find { query -> query.quoteAsset == "USDT" && query.status == "TRADING" && (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol) }?.symbol
            ?: throw MarketNotFoundException("بازاری پیدا نشد")


        if (requestDto.quantity <= 0) throw QuantityNotTrueException("مقدار خرید یا فروش باید بیشتر از صفر باشد")

        if (requestDto.type == Type.MARKET) {
            if (requestDto.side == Side.BUY) {
                val submitOrderRequestDto: SubmitOrderRequestDto = SubmitOrderRequestDto(
                    symbol = existSymbol,
                    quantity = requestDto.quantity.toBigDecimal(),
                    side = Side.BUY,
                    type = Type.MARKET,
                    timestamp = System.currentTimeMillis(),
                    price = null,
                    stopPrice = null,
                )
                val result = tabdealClient.submitOrderInTabdeal(submitOrderRequestDto)
                val userResponse = result?.toMarketOrderUserResponseDto(quantity = requestDto.quantity.toBigDecimal())
                    ?: throw RuntimeException()
                return userResponse
            } else {
                val submitOrderRequestDto: SubmitOrderRequestDto = SubmitOrderRequestDto(
                    symbol = existSymbol,
                    quantity = requestDto.quantity.toBigDecimal(),
                    side = Side.SELL,
                    type = Type.MARKET,
                    timestamp = System.currentTimeMillis(),
                    price = null,
                    stopPrice = null,
                )
                val result = tabdealClient.submitOrderInTabdeal(submitOrderRequestDto)
                val userResponse = result?.toMarketOrderUserResponseDto(quantity = requestDto.quantity.toBigDecimal())
                    ?: throw RuntimeException()
                return userResponse
            }

        } else if (requestDto.type == Type.LIMIT) {
            if (requestDto.side == Side.BUY) {
                val submitOrderRequestDto = SubmitOrderRequestDto(
                    symbol = existSymbol,
                    side = Side.BUY,
                    type = Type.LIMIT,
                    quantity = requestDto.quantity.toBigDecimal(),
                    timestamp = System.currentTimeMillis(),
                    price = requestDto.price,
                    stopPrice = null,
                )
                val result = tabdealClient.submitOrderInTabdeal(submitOrderRequestDto)
                val userResponse = result?.toMarketOrderUserResponseDto(quantity = requestDto.quantity.toBigDecimal())
                    ?: throw RuntimeException()
                return userResponse
            } else {
                val submitOrderRequestDto = SubmitOrderRequestDto(
                    symbol = existSymbol,
                    side = Side.SELL,
                    type = Type.LIMIT,
                    quantity = requestDto.quantity.toBigDecimal(),
                    timestamp = System.currentTimeMillis(),
                    price = requestDto.price,
                    stopPrice = null,
                )
                val result = tabdealClient.submitOrderInTabdeal(submitOrderRequestDto)
                val userResponse = result?.toMarketOrderUserResponseDto(quantity = requestDto.quantity.toBigDecimal())
                    ?: throw RuntimeException()
                return userResponse
            }
        } else {
            if (requestDto.side == Side.BUY) {
                val submitOrderRequestDto = SubmitOrderRequestDto(
                    symbol = existSymbol,
                    side = Side.BUY,
                    type = Type.STOP_LOSS_LIMIT,
                    quantity = requestDto.quantity.toBigDecimal(),
                    timestamp = System.currentTimeMillis(),
                    price = null,
                    stopPrice = requestDto.stopPrice,
                )
                val result = tabdealClient.submitOrderInTabdeal(submitOrderRequestDto)
                val userResponse = result?.toMarketOrderUserResponseDto(quantity = requestDto.quantity.toBigDecimal())
                    ?: throw RuntimeException()
                return userResponse
            } else {
                val submitOrderRequestDto = SubmitOrderRequestDto(
                    symbol = existSymbol,
                    side = Side.SELL,
                    type = Type.STOP_LOSS_LIMIT,
                    quantity = requestDto.quantity.toBigDecimal(),
                    timestamp = System.currentTimeMillis(),
                    price = null,
                    stopPrice = requestDto.stopPrice,
                )
                val result = tabdealClient.submitOrderInTabdeal(submitOrderRequestDto)
                val userResponse = result?.toMarketOrderUserResponseDto(quantity = requestDto.quantity.toBigDecimal())
                    ?: throw RuntimeException()
                return userResponse
            }

        }
    }
}