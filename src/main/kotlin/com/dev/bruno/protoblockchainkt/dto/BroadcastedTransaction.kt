package com.dev.bruno.protoblockchainkt.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class BroadcastedTransaction(
        @NotNull
        val amount: Double,
        @NotNull
        val sender: String,
        @NotNull
        val recipient: String,
        @NotBlank
        val transactionId: String
)