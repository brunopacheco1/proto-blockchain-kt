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

    fun createAndBroadcastTransaction(@Valid newTransaction: NewTransaction): Transaction {
        val (amount, sender, recipient) = newTransaction
        val transaction = createTransaction(amount, sender, recipient)
        networkService.broadcastTransaction(transaction)
        return transaction
    }

    fun addBroadcastedTransaction(@Valid broadcastedTransaction: BroadcastedTransaction): Transaction {
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
                this.blockchain.pendingTransactions.toSet(),
                LocalDateTime.now(),
                this.blockchain.chain.last().hash,
                0,
                ""
        )
    }

    private fun generateProofOfWork(block: Block): Block {
        var nonce = 0L
        var hash = ""
        do {
            nonce++
            hash = generateHash(Block(block.index, block.transactions, block.timestamp, block.previousBlockHash, nonce, hash))
        } while (!hash.startsWith("0000"))
        return Block(block.index, block.transactions, block.timestamp, block.previousBlockHash, nonce, hash)
    }

    fun generateHash(block: Block): String {
        val blockAsString = block.toString()
        val blockBytes = blockAsString.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(blockBytes)
        return digest.fold(
                initial = "",
                operation = { str, it -> str + "%02x".format(it) }
        )
    }

    fun getBlockchain(): Blockchain {
        return blockchain
    }

    fun addBroadcastedBlock(block: Block) {
        if (isValidBlock(block)) {
            addToChain(block)
        }
    }

    private fun isValidBlock(block: Block): Boolean {
        val startsWith0000 = !block.hash.startsWith("0000")
        val hasTheSameGeneratedHash = generateHash(block) != block.hash
        val hasTheSameBlockIndex = block.index != blockchain.chain.size
        return startsWith0000 && hasTheSameGeneratedHash && hasTheSameBlockIndex
    }
}