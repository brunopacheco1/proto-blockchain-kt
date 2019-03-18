package com.dev.bruno.protoblockchainkt.domain

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class Block(
        val index: Int,
        val transactions: List<Transaction>,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        val timestamp: LocalDateTime,
        val previousBlockHash: String,
        val nonce: Long,
        val hash: String
) {

    override fun toString(): String {
        return "Block(index=$index, transactions=$transactions, timestamp=$timestamp, previousBlockHash='$previousBlockHash', nonce=$nonce)"
    }
}