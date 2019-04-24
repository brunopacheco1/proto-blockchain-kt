package com.dev.bruno.protoblockchainkt.dto

import com.dev.bruno.protoblockchainkt.domain.Blockchain
import javax.validation.constraints.NotBlank

data class Node(
        @field:NotBlank
        val nodeUrl: String,
        val blockchain: Blockchain
)