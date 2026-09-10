package com.example.tradershow.controller

import com.example.tradershow.dto.CoinPriceResponseDto
import com.example.tradershow.service.CoinPriceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/price/coins")
class CoinPriceController(private val coinPriceService: CoinPriceService) {


    @GetMapping("/{symbol}")
    fun getPrice(
        @PathVariable("symbol", required = true) symbol: String,
    ): CoinPriceResponseDto {
        return coinPriceService.getCoinPrice(symbol)
    }
}