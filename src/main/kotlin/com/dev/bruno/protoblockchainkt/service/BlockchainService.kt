package com.dev.bruno.protoblockchainkt.service

import com.dev.bruno.protoblockchainkt.domain.Block
import com.dev.bruno.protoblockchainkt.domain.Blockchain
import com.dev.bruno.protoblockchainkt.domain.Transaction
import com.dev.bruno.protoblockchainkt.dto.BroadcastedTransaction
import com.dev.bruno.protoblockchainkt.dto.NewTransaction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*

@Service
class BlockchainService @Autowired constructor(private val networkService: NetworkService) {

    private val blockchain: Blockchain = Blockchain()

    init {
        val genesisBlock = Block(0, listOf(), LocalDateTime.now(), "0", 100, "0")
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
        val newTransaction = NewTransaction(12.5, "00", networkService.getNodeId())
        createAndBroadcastTransaction(newTransaction)
    }

    private fun createBlock(): Block {
        val block = buildBlock()
        val provenBlock = generateProofOfWork(block)
        addToChain(provenBlock)
        return provenBlock
    }

    private fun buildBlock(): Block {
        return Block(
                this.blockchain.chain.size,
                this.blockchain.pendingTransactions.toList(),
                LocalDateTime.now(),
                this.blockchain.chain.last().hash,
                0,
                ""
        )
    }

    private fun generateProofOfWork(block: Block): Block {
        var (nonce, hash) = 0L to ""
        do {
            hash = generateHash(block.copy(hash = hash, nonce = ++nonce))
        } while (!hash.startsWith("0000"))
        return block.copy(hash = hash, nonce = nonce)
    }

    fun generateHash(block: Block): String {
        val blockBytes = block.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(blockBytes)
        return digest.fold(initial = "", operation = { str, it -> str + "%02x".format(it) })
    }

    fun addBroadcastedBlock(block: Block) {
        if (isValidBlock(block)) addToChain(block)
    }

    private fun isValidBlock(block: Block): Boolean {
        val startsWith0000 = block.hash.startsWith("0000")
        val hasTheSameGeneratedHash = generateHash(block) == block.hash
        val hasTheCorrectBlockIndex = block.index == blockchain.chain.size
        return startsWith0000 && hasTheSameGeneratedHash && hasTheCorrectBlockIndex
    }

    fun getBlockchain(): Blockchain {
        return blockchain
    }
}