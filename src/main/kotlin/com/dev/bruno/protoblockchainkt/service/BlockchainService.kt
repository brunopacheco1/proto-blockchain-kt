package com.dev.bruno.protoblockchainkt.service

import com.dev.bruno.protoblockchainkt.domain.Block
import com.dev.bruno.protoblockchainkt.domain.Blockchain
import com.dev.bruno.protoblockchainkt.domain.Transaction
import com.dev.bruno.protoblockchainkt.dto.BroadcastedTransaction
import com.dev.bruno.protoblockchainkt.dto.NewTransaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid

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

    fun createAndBroadcastTransaction(@Valid newTransaction: NewTransaction): Mono<Transaction> {
        val (amount, sender, recipient) = newTransaction
        val transaction = createTransaction(amount, sender, recipient)
        return networkService.broadcastTransaction(transaction)
    }

    fun addBroadcastedTransaction(@Valid broadcastedTransaction: BroadcastedTransaction): Mono<Transaction> {
        val (amount, sender, recipient, transactionId) = broadcastedTransaction
        return createTransaction(amount, sender, recipient, transactionId)
    }

    private fun createTransaction(
            amount: Double,
            sender: String,
            recipient: String,
            transactionId: String = UUID.randomUUID().toString()
    ): Mono<Transaction> {
        val transaction = Transaction(blockchain.chain.size, amount, sender, recipient, transactionId)
        blockchain.pendingTransactions.add(transaction)
        return transaction.toMono()
    }

    fun mine(): Mono<Void> {
        val block = createBlock()
        networkService.broadcastBlock(block)
        val newTransaction = NewTransaction(12.5, "00", networkService.getNodeId())
        val transaction = createAndBroadcastTransaction(newTransaction)
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
                this.blockchain.pendingTransactions.toSet(),
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
        } while (!block.hash.startsWith("0000"))
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