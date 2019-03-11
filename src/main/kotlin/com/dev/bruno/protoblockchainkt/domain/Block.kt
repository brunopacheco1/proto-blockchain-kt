package com.dev.bruno.protoblockchainkt.domain

import java.time.LocalDateTime

data class Block(
        val index: Int,
        val transactions: Set<Transaction>,
        val timestamp: LocalDateTime,
        val previousBlockHash: String,
        var nonce: Long,
        var hash: String
)