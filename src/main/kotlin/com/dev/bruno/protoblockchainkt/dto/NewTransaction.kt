package com.dev.bruno.protoblockchainkt.dto

import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

data class NewTransaction(
        @field:Min(0)
        val amount: Double,
        @field:NotBlank
        val sender: String,
        @field:NotBlank
        val recipient: String
)