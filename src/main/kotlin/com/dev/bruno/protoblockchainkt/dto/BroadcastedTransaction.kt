package com.dev.bruno.protoblockchainkt.dto

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

data class BroadcastedTransaction(
        @field:Min(0)
        val amount: Double,
        @field:NotBlank
        val sender: String,
        @field:NotBlank
        val recipient: String,
        @NotBlank
        val transactionId: String
)