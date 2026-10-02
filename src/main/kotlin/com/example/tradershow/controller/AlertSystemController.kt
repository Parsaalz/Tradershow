package com.example.tradershow.controller

import com.example.tradershow.dto.AlertSystem.CreateAlertRequestDto
import com.example.tradershow.dto.AlertSystem.CreateAlertResponseDto
import com.example.tradershow.service.AlertSystemService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/alerts")
class AlertSystemController(
    private val alertService: AlertSystemService
) {

    @PostMapping
    fun createAlert(
        @RequestBody requestDto: CreateAlertRequestDto
    ): CreateAlertResponseDto
    {
        return alertService.createAlert(requestDto)
    }
}