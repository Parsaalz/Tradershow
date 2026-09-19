package com.example.tradershow

import com.example.tradershow.service.SymbolNormalizerService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SymbolNormalizerTest {
    val normalizer = SymbolNormalizerService()
    @Test
    fun `test its works successfully with just character`(){
        val symbol = normalizer.normalize("BTC")
        assertEquals("BTC", symbol)
    }


    @Test
    fun `test it does not work`(){
        val symbol = normalizer.normalize("BTC-BTC")
        assertNotEquals("BTC-BTC", symbol)
    }
}