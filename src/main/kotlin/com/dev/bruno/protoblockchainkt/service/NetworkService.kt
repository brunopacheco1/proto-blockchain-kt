package com.dev.bruno.protoblockchainkt.service

import com.dev.bruno.protoblockchainkt.domain.Block
import com.dev.bruno.protoblockchainkt.domain.Transaction
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class NetworkService {

    val network: HashSet<String> = hashSetOf()

    fun broadcastTransaction(transaction: Mono<Transaction>): Mono<Transaction> {
        return transaction
    }

    fun broadcastBlock(block: Mono<Block>): Mono<Block> {
        return block
    }

    fun getNodeId(): String {
        return UUID.randomUUID().toString()
    }
}