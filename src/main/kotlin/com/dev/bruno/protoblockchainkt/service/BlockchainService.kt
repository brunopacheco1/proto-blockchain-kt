package com.dev.bruno.protoblockchainkt.service

import com.dev.bruno.protoblockchainkt.domain.Block
import com.dev.bruno.protoblockchainkt.domain.Blockchain
import com.dev.bruno.protoblockchainkt.domain.Transaction
import com.dev.bruno.protoblockchainkt.dto.BroadcastedTransaction
import com.dev.bruno.protoblockchainkt.dto.NewTransaction
import com.dev.bruno.protoblockchainkt.helper.generateHash
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class BlockchainService @Autowired constructor(private val networkService: NetworkService) {

    private val HASH_PREFIX = "0000"
    private val blockchain: Blockchain = Blockchain()

    init {
        val genesisBlock = buildBlock()
        addToChain(genesisBlock)
    }

    private fun addToChain(block: Block) {
        blockchain.pendingTransactions.clear()
        blockchain.chain.add(block)
    }

    fun createAndBroadcastTransaction(newTransaction: NewTransaction): Transaction {
        val (amount, sender, recipient) = newTransaction
        val transaction = createTransaction(amount, sender, recipient)
        networkService.broadcastTransaction(transaction)
        return transaction
    }

    fun addBroadcastedTransaction(broadcastedTransaction: BroadcastedTransaction): Transaction {
        val (amount, sender, recipient, transactionId) = broadcastedTransaction
        return createTransaction(amount, sender, recipient, transactionId)
    }

    private fun createTransaction(
            amount: Double,
            sender: String,
            recipient: String,
            transactionId: String = UUID.randomUUID().toString()
    ): Transaction {
        val transaction = Transaction(blockchain.chain.size, amount, sender, recipient, transactionId)
        blockchain.pendingTransactions.add(transaction)
        return transaction
    }

    fun mine() {
        val block = createBlock()
        networkService.broadcastBlock(block)
        val miningReward = NewTransaction(12.5, "00", networkService.getNodeId())
        createAndBroadcastTransaction(miningReward)
    }

    private fun createBlock(): Block {
        val block = buildBlock()
        val provenBlock = generateProofOfWork(block)
        addToChain(provenBlock)
        return provenBlock
    }

    private fun buildBlock() = Block(
            this.blockchain.chain.size,
            this.blockchain.pendingTransactions.toList(),
            LocalDateTime.now(),
            this.blockchain.chain.lastOrNull()?.hash ?: "0",
            100L,
            "0"
    )

    private fun generateProofOfWork(block: Block): Block {
        var (nonce, hash) = 0L to ""
        do {
            hash = block.copy(hash = hash, nonce = ++nonce).generateHash()
        } while (!hash.startsWith(HASH_PREFIX))
        return block.copy(hash = hash, nonce = nonce)
    }

    fun addBroadcastedBlock(block: Block) {
        if (isValidToAdd(block)) addToChain(block)
    }

    fun isValidToAdd(block: Block): Boolean {
        val startsWith0000 = block.hash.startsWith(HASH_PREFIX)
        val hasTheSameGeneratedHash = block.generateHash() == block.hash
        val hasTheCorrectBlockIndex = block.index == blockchain.chain.size
        return startsWith0000 && hasTheSameGeneratedHash && hasTheCorrectBlockIndex
    }

    fun getBlockchain() = blockchain
}