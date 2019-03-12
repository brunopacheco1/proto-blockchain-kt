package com.dev.bruno.protoblockchainkt.domain

import java.util.*

data class Network(
        val nodes: HashSet<String> = hashSetOf(),
        val nodeId: String = UUID.randomUUID().toString()
)