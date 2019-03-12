package com.dev.bruno.protoblockchainkt.service

import com.dev.bruno.protoblockchainkt.domain.Block
import com.dev.bruno.protoblockchainkt.domain.Blockchain
import com.dev.bruno.protoblockchainkt.domain.Transaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashSet

@Service
class BlockchainService @Autowired constructor(private val networkService: NetworkService) {

    private val blockchain: Blockchain = Blockchain()

    init {
        val genesisBlock = Block(0, setOf(), LocalDateTime.now(), "0", 100, "0")
        addToChain(genesisBlock)
    }

    private fun addToChain(block: Block) {
        blockchain.pendingTransactions.clear()
        blockchain.chain.add(block)
    }

    fun createAndBroadcastTransaction(
            amount: Double,
            sender: String,
            recipient: String
    ): Mono<Transaction> {
        val transaction = createTransaction(amount, sender, recipient)
        return networkService.broadcastTransaction(transaction)
    }

    fun createTransaction(
            amount: Double,
            sender: String,
            recipient: String,
            transactionId: String? = UUID.randomUUID().toString()
    ): Mono<Transaction> {
        val transaction = Transaction(blockchain.chain.size, amount, sender, recipient, transactionId)
        blockchain.pendingTransactions.add(transaction)
        return transaction.toMono()
    }

    fun mine(): Mono<Void> {
        val block = createBlock()
        networkService.broadcastBlock(block)
        val transaction = createAndBroadcastTransaction(12.5, "00", networkService.getNodeId())
        return block.and(transaction)
    }

    private fun createBlock(): Mono<Block> {
        val block = buildBlock()
        generateProofOfWork(block)
        addToChain(block)
        return block.toMono()
    }

    private fun buildBlock(): Block {
        return Block(
                this.blockchain.chain.size,
                HashSet<Transaction>(this.blockchain.pendingTransactions),
                LocalDateTime.now(),
                this.blockchain.chain.last().hash,
                0,
                ""
        )
    }

    private fun generateProofOfWork(block: Block) {
        do {
            block.nonce++
            generateHash(block)
        } while (!block.hash!!.startsWith("0000"))
    }

    private fun generateHash(block: Block) {
        val blockBytes = block.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(blockBytes)
        block.hash = digest.fold(
                initial = "",
                operation = { str, it -> str + "%02x".format(it) }
        )
    }

    fun getBlockchain(): Mono<Blockchain> {
        return blockchain.toMono()
    }
}