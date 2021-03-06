package com.dev.bruno.protoblockchainkt.service

import com.dev.bruno.protoblockchainkt.domain.Block
import com.dev.bruno.protoblockchainkt.domain.BlockchainConstants
import com.dev.bruno.protoblockchainkt.domain.Transaction
import com.dev.bruno.protoblockchainkt.dto.BroadcastedTransaction
import com.dev.bruno.protoblockchainkt.dto.NewTransaction
import com.dev.bruno.protoblockchainkt.dto.Node
import com.dev.bruno.protoblockchainkt.helper.generateHash
import com.dev.bruno.protoblockchainkt.repository.BlockchainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class BlockchainService @Autowired constructor(
        private val networkService: NetworkService,
        private val repository: BlockchainRepository
) {

    init {
        val blockchain = repository.getBlockchain()
        if (blockchain.chain.isEmpty()) {
            val genesisBlock = buildBlock()
            addToChain(genesisBlock)
        }
    }

    private fun addToChain(block: Block) {
        val blockchain = repository.getBlockchain()
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
        val blockchain = repository.getBlockchain()
        val transaction = Transaction(blockchain.chain.size, amount, sender, recipient, transactionId)
        blockchain.pendingTransactions.add(transaction)
        return transaction
    }

    fun mine() {
        val block = createBlock()
        networkService.broadcastBlock(block)
        val miningReward = NewTransaction(BlockchainConstants.DEFAULT_MINING_REWARD, BlockchainConstants.DEFAULT_SENDER, networkService.getNodeId())
        createAndBroadcastTransaction(miningReward)
    }

    private fun createBlock(): Block {
        val block = buildBlock()
        val provenBlock = generateProofOfWork(block)
        addToChain(provenBlock)
        return provenBlock
    }

    private fun buildBlock(): Block {
        val blockchain = repository.getBlockchain()
        val previousBlockHash = blockchain.chain.lastOrNull()?.hash ?: BlockchainConstants.DEFAULT_HASH
        return Block(
                blockchain.chain.size, blockchain.pendingTransactions.toList(), LocalDateTime.now(),
                previousBlockHash, BlockchainConstants.DEFAULT_NONCE, BlockchainConstants.DEFAULT_HASH
        )
    }

    private fun generateProofOfWork(block: Block): Block {
        var nonce = 0L
        var hash: String
        do {
            hash = block.copy(nonce = ++nonce).generateHash()
        } while (!hash.startsWith(BlockchainConstants.HASH_PREFIX))
        return block.copy(hash = hash, nonce = nonce)
    }

    fun addBroadcastedBlock(block: Block) {
        if (isValidToAdd(block)) addToChain(block)
    }

    fun isValidToAdd(block: Block): Boolean {
        val blockchain = repository.getBlockchain()
        val startsWith0000 = block.hash.startsWith(BlockchainConstants.HASH_PREFIX)
        val hasTheSameGeneratedHash = block.generateHash() == block.hash
        val hasTheCorrectBlockIndex = block.index == blockchain.chain.size
        return startsWith0000 && hasTheSameGeneratedHash && hasTheCorrectBlockIndex
    }

    fun getBlockchain() = repository.getBlockchain()

    fun consensus(newNode: Node) {
        networkService.registerNode(newNode)
        val newNodeHasLongerChain = newNode.blockchain.chain.size > repository.getBlockchain().chain.size
        if (newNodeHasLongerChain && newNode.blockchain.isValid()) {
            repository.setBlockchain(newNode.blockchain)
        }
    }
}