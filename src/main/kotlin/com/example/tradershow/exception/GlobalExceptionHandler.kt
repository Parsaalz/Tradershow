package com.example.tradershow.exception

import com.example.tradershow.dto.CoinPriceResponseDto
import com.example.tradershow.dto.ErrorResponseDto
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice



@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MarketNotFoundException::class)
    fun handleMarketNotFound(
        exception: MarketNotFoundException,
        request: HttpServletRequest,
    ):ResponseEntity<ErrorResponseDto>
    {
        val response= ErrorResponseDto(
            status=400,
            message=exception.message?:"not found",
            path=request.requestURI,
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @ExceptionHandler(TabdealApiException::class)
    fun handleTabdealApiException(
        exception: TabdealApiException,
        request: HttpServletRequest,
    ):ResponseEntity<ErrorResponseDto>
    {
        val response= ErrorResponseDto(
            status=502,
            message=exception.message?:"bad gateway",
            path=request.requestURI,
        )

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response)
    }



}