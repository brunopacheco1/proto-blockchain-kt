package com.dev.bruno.protoblockchainkt.domain

data class Transaction(
        val blockIndex: Int,
        val amount: Double,
        val sender: String,
        val recipient: String,
        val transactionId: String
) {

    override fun toString(): String {
        return "Transaction(blockIndex=$blockIndex, amount=$amount, sender='$sender', recipient='$recipient', transactionId='$transactionId')"
    }
}