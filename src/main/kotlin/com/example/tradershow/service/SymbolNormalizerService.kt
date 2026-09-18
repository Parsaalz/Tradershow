package com.example.tradershow.service

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class SymbolNormalizerService {
    fun normalize(userSymbol: String): String {
        val normalizedUserSymbol: String = userSymbol.trim().let {
            var newSymbol = ""
            for (i in it.indices) {
                if (it[i] != ('/') && it[i] != ('-') && it[i] != ('_')) {
                    newSymbol += it[i]
                }
            }
            newSymbol.uppercase()
        }
        return normalizedUserSymbol
    }
}