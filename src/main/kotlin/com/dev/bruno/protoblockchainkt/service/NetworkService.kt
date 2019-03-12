package com.dev.bruno.protoblockchainkt.service

import com.dev.bruno.protoblockchainkt.domain.Block
import com.dev.bruno.protoblockchainkt.domain.Network
import com.dev.bruno.protoblockchainkt.domain.Transaction
import org.springframework.stereotype.Service

@Service
class NetworkService {

    private val network: Network = Network()

    fun broadcastTransaction(transaction: Transaction) {
    }

    fun broadcastBlock(block: Block) {
    }

    fun getNetwork(): Network {
        return network
    }

    fun getNodeId(): String {
        return network.nodeId
    }
}