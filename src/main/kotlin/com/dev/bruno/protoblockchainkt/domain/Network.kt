package com.dev.bruno.protoblockchainkt.domain

data class Network(
        val nodes: HashSet<String>,
        val nodeId: String
)