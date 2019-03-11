package com.dev.bruno.protoblockchainkt.domain

data class Blockchain(
        val chain: HashSet<Block> = hashSetOf(),
        val pendingTransactions: HashSet<Transaction> = hashSetOf()
)