package com.example.tradershow.service

import com.example.tradershow.client.TabdealClient
import com.example.tradershow.database.table.OrderStatus
import com.example.tradershow.database.table.StopLossState
import com.example.tradershow.dto.CancellOrder.CancellOrderRequestDto
import com.example.tradershow.dto.CancellOrder.TabdealCancellOrderDtoResponse
import com.example.tradershow.dto.CoinPriceResponseDto
import com.example.tradershow.dto.Conditional.ConditionalOrderRequestDto
import com.example.tradershow.dto.Conditional.ConditionalOrderUserResponseDto
import com.example.tradershow.dto.Limit.LimitOrderRequestDto
import com.example.tradershow.dto.Limit.LimitOrderUserResponseDto
import com.example.tradershow.dto.LotSize
import com.example.tradershow.dto.Market.MarketOrderRequestDto
import com.example.tradershow.dto.Market.MarketOrderUserResponseDto
import com.example.tradershow.dto.Market.Side
import com.example.tradershow.dto.Market.SubmitMarketOrderRequestDto
import com.example.tradershow.dto.Market.Type
import com.example.tradershow.dto.MinNotional
import com.example.tradershow.dto.StopLoss.StopLossOrderUserRequest
import com.example.tradershow.dto.StopLoss.StopLossUserResponseDto
import com.example.tradershow.exception.MarketNotFoundException
import com.example.tradershow.repository.CoinPricesRepository
import com.example.tradershow.repository.OrderRepository
import com.example.tradershow.repository.StopLossRepository
import org.springframework.core.annotation.Order
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.concurrent.Executors
import kotlin.let
import kotlin.text.toBigDecimal
import kotlin.toBigDecimal

@Service
class OrderService(
    private val normalizerService: SymbolNormalizerService,
    private val symbolAliasService: SymbolAliasService,
    private val tabdealClient: TabdealClient,
    private val orderRepository: OrderRepository,
    private val stopLossRepository: StopLossRepository,
    private val coinPricesRepository: CoinPricesRepository,
) {
    fun submitMarketOrder(requestDto: MarketOrderRequestDto): MarketOrderUserResponseDto {
        val currentTimeStamp = System.currentTimeMillis()

        requestDto.validate()

        val normalizedSymbol = normalizerService.normalize(requestDto.symbol)


        val alizedSymbol = symbolAliasService.searchAlias(normalizedSymbol)


        val markets = tabdealClient.getExchangeInfo()
        val existSymbol = markets.find { query ->
            query.quoteAsset == "USDT" && query.status == "TRADING" && (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: markets.find { query ->
            query.quoteAsset == "IRT" && query.status == "TRADING" && (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: throw MarketNotFoundException("بازاری پیدا نشد")


        if (requestDto.quantity.toBigDecimal() < (markets.find { it.symbol == existSymbol }?.filters?.filterIsInstance<LotSize>()
                ?.firstOrNull()?.minQty?.toBigDecimal() ?: BigDecimal.ZERO)
        ) {
            throw MarketNotFoundException("مقدار ورودی مقدار کمتر از حد پایین می باشد برای خرید لطفا در خرید خود توجه کنید ")
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
        var status = OrderStatus.FAILED
        when (userResponse.status) {
            "FILLED" -> status = OrderStatus.FAILED
            "NEW" -> status = OrderStatus.PENDING
            "PARTIALLY_FILLED" -> status = OrderStatus.EXECUTING
            "CANCELED" -> status = OrderStatus.CANCELLED
            "REJECTED" -> status = OrderStatus.FAILED
        }
        orderRepository.save(
            userResponse.symbol,
            userResponse.side,
            userResponse.type,
            userResponse.quantity.toBigDecimal(),

            userResponse.orderId,
            status,
            currentTimeStamp
        )
        return userResponse
    }

    fun submitLimitOrder(requestDto: LimitOrderRequestDto): LimitOrderUserResponseDto {
        val currentTimeStamp = System.currentTimeMillis()
        var stop_loss_limit: Boolean = false
        if (requestDto.type == Type.STOP_LOSS_LIMIT) {
            stop_loss_limit = true
        }
        val normalizedSymbol = normalizerService.normalize(requestDto.symbol)
        val alizedSymbol = symbolAliasService.searchAlias(normalizedSymbol)
        val markets = tabdealClient.getExchangeInfo()
        val existSymbol = markets.find { query ->
            query.quoteAsset == "USDT" && query.status == "TRADING" && (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: markets.find { query ->
            query.quoteAsset == "IRT" && query.status == "TRADING" && (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: throw MarketNotFoundException("بازاری پیدا نشد")
        if (requestDto.quantity < (markets.find { it.symbol == existSymbol }?.filters?.filterIsInstance<LotSize>()
                ?.firstOrNull()?.minQty?.toBigDecimal() ?: BigDecimal.ZERO)
        ) {
            throw MarketNotFoundException("مقدار ورودی مقدار کمتر از حد پایین می باشد برای خرید لطفا در خرید خود توجه کنید ")
        }
        val newRequestDto = requestDto.toSubmitLimitOrderRequestDto(timestamp = System.currentTimeMillis(), existSymbol)
        val result = tabdealClient.submitLimitOrder(newRequestDto)
        var status = OrderStatus.FAILED
        when (result.status) {
            "FILLED" -> status = OrderStatus.FAILED
            "NEW" -> status = OrderStatus.PENDING
            "PARTIALLY_FILLED" -> status = OrderStatus.EXECUTING
            "CANCELED" -> status = OrderStatus.CANCELLED
            "REJECTED" -> status = OrderStatus.FAILED
        }
        if (stop_loss_limit) {
            println("first")
            orderRepository.save(
                result.symbol,
                result.side,
                Type.STOP_LOSS_LIMIT.toString(),
                result.cummulativeQuoteQty.toBigDecimal(),
                result.orderId,
                status,
                currentTimeStamp

            )
        } else {
            println("first")
            orderRepository.save(
                result.symbol,
                result.side,
                result.type,
                result.cummulativeQuoteQty.toBigDecimal(),
                result.orderId,
                status,
                currentTimeStamp
            )
        }

        return result.toLimitOrderUserResponseDto()
    }


    fun submitConditionalOrder(requestDto: ConditionalOrderRequestDto): ConditionalOrderUserResponseDto {
        val currentTimeStamp = System.currentTimeMillis()
        val normalizedSymbol = normalizerService.normalize(requestDto.symbol)
        val alizedSymbol = symbolAliasService.searchAlias(normalizedSymbol)
        val markets = tabdealClient.getExchangeInfo()
        val existSymbol = markets.find { query ->
            query.quoteAsset == "USDT" && query.status == "TRADING" && (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: markets.find { query ->
            query.quoteAsset == "IRT" && query.status == "TRADING" && (query.symbol == alizedSymbol || query.baseAsset == alizedSymbol)
        }?.symbol ?: throw MarketNotFoundException("بازاری پیدا نشد")
        if (requestDto.quantity.toBigDecimal() < (markets.find { it.symbol == existSymbol }?.filters?.filterIsInstance<LotSize>()
                ?.firstOrNull()?.minQty?.toBigDecimal() ?: BigDecimal.ZERO)
        ) {
            throw MarketNotFoundException("مقدار ورودی مقدار کمتر از حد پایین می باشد برای خرید لطفا در خرید خود توجه کنید ")
        }

        val newRequestDto = requestDto.toSubmitConditionalRequestDto(
            timestamp = System.currentTimeMillis(),
            symbol = existSymbol,
        )
        val result = tabdealClient.submitConditionalOrder(newRequestDto).toConditionalOrderUserResponseDto()
        var status = OrderStatus.FAILED
        when (result.status) {
            "FILLED" -> status = OrderStatus.FAILED
            "NEW" -> status = OrderStatus.PENDING
            "PARTIALLY_FILLED" -> status = OrderStatus.EXECUTING
            "CANCELED" -> status = OrderStatus.CANCELLED
            "REJECTED" -> status = OrderStatus.FAILED
        }
        orderRepository.save(
            result.symbol,
            result.side,
            result.type,
            result.cummulativeQuoteQty.toBigDecimal(),
            result.orderId,
            status,
            currentTimeStamp
        )
        return result

    }


    fun submitStopLossLimitOrderService(requestDto: StopLossOrderUserRequest): StopLossUserResponseDto {
        val timestamp = System.currentTimeMillis()
        stopLossRepository.save(
            requestDto.symbol,
            requestDto.quantity,
            requestDto.stopLossPrice,
            requestDto.price,
            timestamp,
            requestDto.side,
            requestDto.type,
            coinPricesRepository.findBySymbol(requestDto.symbol)?.price ?: BigDecimal.ZERO,
            StopLossState.PENDING
        )
        return requestDto.toStopLossUserResponse(timestamp)

    }

    fun cancellOrder(requestDto: CancellOrderRequestDto): TabdealCancellOrderDtoResponse {
        val result = tabdealClient.cancelOrder(requestDto.symbol, requestDto.orderId.toLong())
        if (result.status == "CANCELED") {
            val row = orderRepository.getOrderById(result.orderId.toLong())
            orderRepository.updateOrderById(row.orderId.toLong(), status = OrderStatus.CANCELLED)

        }
        return result
    }

    // TODO(implement cancell all orders)
    fun cancellAllOpenOrders() {

    }
}


@Component
class CheckStopLossOrderService(
    private val stopLossRepo: StopLossRepository,
    private val orderService: OrderService,
    private val coinPriceRepo: CoinPricesRepository,
    private val orderRepo: OrderRepository,
    private val tabdealClient: TabdealClient,
) {
    @Scheduled(fixedRate = 10_000)
    fun checkOrders() {
        println("start check orders")

        val executer = Executors.newScheduledThreadPool(10)


        val result = orderRepo.getAllOrders()

        result.forEach { order ->
            executer.submit {
                when (order.status) {
                    OrderStatus.PENDING.toString(), OrderStatus.TRIGERRED.toString(), OrderStatus.EXECUTING.toString() -> {
                        val status = tabdealClient.getOrderStatus(order.symbol, order.orderId.toLong())
                        if (status.status == "NEW") {
                            orderRepo.updateOrderById(order.orderId.toLong(), status = OrderStatus.PENDING)
                        } else if (status.status == "PARTIALLY_FILLED") {
                            orderRepo.updateOrderById(order.orderId.toLong(), status = OrderStatus.EXECUTING)
                        } else if (status.status == "FILLED") {
                            orderRepo.updateOrderById(order.orderId.toLong(), status = OrderStatus.SUCCESS)
                        } else if (status.status == "CANCELED") {
                            orderRepo.updateOrderById(order.orderId.toLong(), status = OrderStatus.CANCELLED)
                        } else if (status.status == "REJECTED") {
                            orderRepo.updateOrderById(order.orderId.toLong(), status = OrderStatus.FAILED)
                        }
                    }
                    else -> {

                    }
                }
            }
        }
//        val result = stopLossRepo.findByState(StopLossState.PENDING)
//
//        result.forEach { record ->
//            val price = coinPriceRepo.findBySymbol(record.symbol)?.price ?: BigDecimal.ZERO
//
//            if (record.stopPrice > record.currentPrice && record.stopPrice >=price){
//                executer.submit {
//                    try {
//                        val requestDto = LimitOrderRequestDto(
//                            record.symbol,
//                            Side.BUY,
//                            record.quantity,
//                            Type.STOP_LOSS_LIMIT,
//                            record.price,
//                        )
//                        try {
//                            val result = orderService.submitLimitOrder(requestDto)
//                        }catch (e:Exception){
//                            stopLossRepo.updateState(record.id, StopLossState.FAILED)
//                        }
//                        stopLossRepo.updateState(record.id, StopLossState.TRIGGERED)
//
//                    }catch (ex:Throwable){
//                        stopLossRepo.updateState(record.id, StopLossState.FAILED)
//                        throw ex
//                    }
//                }
//            }
//            else if(record.stopPrice < record.currentPrice && record.stopPrice <=price){
//                executer.submit {
//                    try {
//                        val requestDto = LimitOrderRequestDto(
//                            record.symbol,
//                            Side.SELL,
//                            record.quantity,
//                            Type.STOP_LOSS_LIMIT,
//                            record.price,
//                        )
//                        val result = orderService.submitLimitOrder(requestDto)
//                        println(result)
//                        stopLossRepo.updateState(record.id, StopLossState.TRIGGERED)
//                    }catch (ex:Throwable){
//                        println(ex)
//                        stopLossRepo.updateState(record.id, StopLossState.FAILED)
//                        throw ex
//                    }
//                }
//            }
//            else if (record.result == StopLossState.TRIGGERED)
//            {
//
//            }
//        }
        println("finished check orders")
        executer.shutdown()
    }
}











