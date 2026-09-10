package com.example.tradershow.service

import org.springframework.stereotype.Service


@Service
class SymbolAliasService {
    private val aliases = mapOf(
        // Bitcoin
        "BITCOIN" to "BTC",
        "BTC" to "BTC",

        // Ethereum
        "ETHEREUM" to "ETH",
        "ETHER" to "ETH",
        "ETH" to "ETH",

        // Tether
        "TETHER" to "USDT",
        "USDT" to "USDT",

        // Solana
        "SOLANA" to "SOL",
        "SOL" to "SOL",

        // Dogecoin
        "DOGECOIN" to "DOGE",
        "DOGE" to "DOGE",

        // XRP
        "RIPPLE" to "XRP",
        "XRP" to "XRP",

        // Cardano
        "CARDANO" to "ADA",
        "ADA" to "ADA",

        // Tron
        "TRON" to "TRX",
        "TRX" to "TRX",

        // BNB
        "BINANCECOIN" to "BNB",
        "BNB" to "BNB",

        // Avalanche
        "AVALANCHE" to "AVAX",
        "AVAX" to "AVAX",

        // Polkadot
        "POLKADOT" to "DOT",
        "DOT" to "DOT",

        // Polygon
        "POLYGON" to "POL",
        "MATIC" to "POL",
        "POL" to "POL",

        // Chainlink
        "CHAINLINK" to "LINK",
        "LINK" to "LINK",

        // Litecoin
        "LITECOIN" to "LTC",
        "LTC" to "LTC",

        // Bitcoin Cash
        "BITCOINCASH" to "BCH",
        "BCH" to "BCH",

        // Stellar
        "STELLAR" to "XLM",
        "XLM" to "XLM",

        // Uniswap
        "UNISWAP" to "UNI",
        "UNI" to "UNI",

        // Cosmos
        "COSMOS" to "ATOM",
        "ATOM" to "ATOM",

        // Near
        "NEAR" to "NEAR",

        // Aptos
        "APTOS" to "APT",
        "APT" to "APT",

        // Arbitrum
        "ARBITRUM" to "ARB",
        "ARB" to "ARB",

        // Optimism
        "OPTIMISM" to "OP",
        "OP" to "OP",

        // Filecoin
        "FILECOIN" to "FIL",
        "FIL" to "FIL",

        // Internet Computer
        "INTERNETCOMPUTER" to "ICP",
        "ICP" to "ICP",

        // Cosmos
        "COSMOS" to "ATOM",
        "ATOM" to "ATOM",

        // Aave
        "AAVE" to "AAVE",

        // Shiba Inu
        "SHIBAINU" to "SHIB",
        "SHIBA" to "SHIB",
        "SHIB" to "SHIB",

        // Dai
        "DAI" to "DAI",

        // Unus Sed Leo
        "UNUSSEDLEO" to "LEO",
        "LEO" to "LEO",

        // Ethereum Classic
        "ETHEREUMCLASSIC" to "ETC",
        "ETC" to "ETC",

        // Monero
        "MONERO" to "XMR",
        "XMR" to "XMR",

        // Algorand
        "ALGORAND" to "ALGO",
        "ALGO" to "ALGO",

        // VeChain
        "VECHAIN" to "VET",
        "VET" to "VET",

        // Hedera
        "HEDERA" to "HBAR",
        "HBAR" to "HBAR",

        // Filecoin
        "FILECOIN" to "FIL",
        "FIL" to "FIL"
    )

    fun searchAlias(symbol: String): String {
        if(aliases.keys.contains(symbol))
            return aliases[symbol]!!
        return symbol
    }
}