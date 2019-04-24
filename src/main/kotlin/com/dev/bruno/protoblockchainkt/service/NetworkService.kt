package com.dev.bruno.protoblockchainkt.service

import com.dev.bruno.protoblockchainkt.domain.Block
import com.dev.bruno.protoblockchainkt.domain.Network
import com.dev.bruno.protoblockchainkt.domain.Transaction
import com.dev.bruno.protoblockchainkt.dto.Node
import org.springframework.stereotype.Service

@Service
class NetworkService {

    private val network: Network = Network()

    fun broadcastTransaction(transaction: Transaction) {
    }

    fun broadcastBlock(block: Block) {
    }

    fun registerNode(node: Node) = network.nodes.add(node.nodeUrl)

    fun getNetwork() = network

    fun getNodeId() = network.nodeId
}