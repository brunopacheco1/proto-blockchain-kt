package com.dev.bruno.protoblockchainkt.domain

import javax.validation.constraints.NotNull

data class Transaction(
        val blockIndex: Int?,
        @NotNull
        val amount: Double,
        @NotNull
        val sender: String,
        @NotNull
        val recipient: String,
        val transactionId: String?
) {

    override fun toString(): String {
        return "Transaction(blockIndex=$blockIndex, amount=$amount, sender='$sender', recipient='$recipient', transactionId='$transactionId')"
    }
}