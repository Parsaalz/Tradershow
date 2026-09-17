package com.example.tradershow

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.dto.ExchangeInfoResponseDto
import com.example.tradershow.dto.FillDto
import com.example.tradershow.dto.Market.MarketOrderRequestDto
import com.example.tradershow.dto.Market.MarketOrderUserResponseDto
import com.example.tradershow.dto.Market.MarketOrderResponseDto
import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.SubmitMarketOrderRequestDto
import com.example.tradershow.dto.Market.Type
import com.example.tradershow.repository.OrderRepository
import com.example.tradershow.service.OrderService
import com.example.tradershow.service.SymbolAliasService
import com.example.tradershow.service.SymbolNormalizerService
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderServiceTest {

    private val tabdealClient = mockk<TabdealClient>()
    private val orderRepository = mockk<OrderRepository>()
    private val symbolNormalizer = mockk<SymbolNormalizerService>()
    private val symbolAliasService = mockk<SymbolAliasService>()
    private val orderService = OrderService(
        normalizerService = symbolNormalizer,
        symbolAliasService = symbolAliasService,
        tabdealClient = tabdealClient,
        orderRepository = orderRepository
    )

    @Test
    fun `should return correct order response`() {
        every {symbolNormalizer.normalize("BTCUSDT") } returns "BTCUSDT"

        every {symbolAliasService.searchAlias("BTCUSDT") } returns "BTCUSDT"



        val responseGetExchangeDto= ExchangeInfoResponseDto(
            symbol = "BTCUSDT",
            tabdealSymbol = "BTCUSDT",
            status = "TRADING",
            baseAsset = "BTC",
            quoteAsset = "USDT",
            filters = listOf()
        )


        every { tabdealClient.getExchangeInfo() } returns listOf(responseGetExchangeDto)

        every {
            orderRepository.save(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } just Runs


        val requestDtoTabdeal = SubmitMarketOrderRequestDto(
            symbol = "BTCUSDT",
            side = Side.BUY,
            type = Type.MARKET,
            quantity = BigDecimal("1.5"),
            timestamp = 123456789L,
            price = BigDecimal("1.5"),
            stopPrice = BigDecimal("1.5"),
        )
        val requestServiceDto= MarketOrderRequestDto(
            symbol = "BTCUSDT",
            side = Side.BUY,
            type = Type.MARKET,
            quantity = 1.5,
            price = BigDecimal("1.5"),
            stopPrice = BigDecimal("1.5"),
        )

        val responseDto = MarketOrderResponseDto(
            symbol = "BTCUSDT",
            side = "BUY",
            type = "LIMIT",
            orderId = 123456789,
            transactTime = 123456789L,
            origQty = "0.10000",
            executedQty = "0.10000",
            cummulativeQuoteQty = "99999.99900000",
            status = "FILLED",
            price = "0.0000000",
            fills = listOf(
                FillDto(
                    price = "0.0000",
                    qty = "0.10000",
                    commission = "0.0001",
                    commissionAsset = "USDT",
                    tradeId = 123456789L
                )
            )
        )
        val serviceResponseDto= MarketOrderUserResponseDto(
            symbol = "BTCUSDT",
            side = "BUY",
            type = "LIMIT",
            orderId = 123456789,
            quantity = "0.10000",
            price = "0.0000000",
            executedQuantity = "0.10000",
            total = "99999.99900000",
            status = "FILLED",
        )

        every { tabdealClient.submitOrderInTabdeal(any()) } returns responseDto


        val result = orderService.submitMarketOrder(requestServiceDto)

        assertEquals(serviceResponseDto, result)


    }



}