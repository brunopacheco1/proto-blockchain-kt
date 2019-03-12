package com.dev.bruno.protoblockchainkt.domain

data class Blockchain(
        val chain: LinkedHashSet<Block> = linkedSetOf(),
        val pendingTransactions: HashSet<Transaction> = hashSetOf()
)