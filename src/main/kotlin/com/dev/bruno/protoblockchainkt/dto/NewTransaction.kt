package com.dev.bruno.protoblockchainkt.dto

import javax.validation.constraints.NotNull

data class NewTransaction(
        @NotNull
        val amount: Double,
        @NotNull
        val sender: String,
        @NotNull
        val recipient: String
)