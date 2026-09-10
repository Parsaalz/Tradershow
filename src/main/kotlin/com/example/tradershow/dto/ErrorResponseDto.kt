package com.example.tradershow.dto

data class ErrorResponseDto(
    val status: Int,
    val message: String,
    val path: String,
)
