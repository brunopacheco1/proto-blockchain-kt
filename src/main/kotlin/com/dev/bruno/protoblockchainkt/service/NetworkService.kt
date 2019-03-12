package com.dev.bruno.protoblockchainkt.service

import com.dev.bruno.protoblockchainkt.domain.Block
import com.dev.bruno.protoblockchainkt.domain.Network
import com.dev.bruno.protoblockchainkt.domain.Transaction
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.*

@Service
class NetworkService {

    private val network: Network = Network(hashSetOf(), UUID.randomUUID().toString())

    fun broadcastTransaction(transaction: Mono<Transaction>): Mono<Transaction> {
        return transaction
    }

    fun broadcastBlock(block: Mono<Block>): Mono<Block> {
        return block
    }

    fun getNetwork(): Mono<Network> {
        return network.toMono()
    }

    fun getNodeId(): String {
        return network.nodeId
    }
}