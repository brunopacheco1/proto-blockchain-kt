package com.dev.bruno.protoblockchainkt

import com.dev.bruno.protoblockchainkt.domain.BlockchainConstants
import com.dev.bruno.protoblockchainkt.dto.BroadcastedTransaction
import com.dev.bruno.protoblockchainkt.dto.NewTransaction
import com.dev.bruno.protoblockchainkt.dto.Node
import com.dev.bruno.protoblockchainkt.helper.generateHash
import com.dev.bruno.protoblockchainkt.repository.BlockchainRepository
import com.dev.bruno.protoblockchainkt.service.BlockchainService
import com.dev.bruno.protoblockchainkt.service.NetworkService
import org.junit.Before
import org.junit.Test

class BlockchainServiceTest {

    private val AMOUNT = 10.0
    private val SENDER = "Sender Test"
    private val RECIPIENT = "Recipient Test"
    private val TRANSACTION_ID = "Transaction Id"
    private val BLOCK_INDEX = 1
    private val DUMMY_NODE = "DUMMY NODE"
    private var repository = BlockchainRepository()
    private var objectUnderTest = BlockchainService(NetworkService(), repository)

    @Before
    fun setUp() {
        repository = BlockchainRepository()
        objectUnderTest = BlockchainService(NetworkService(), repository)
    }

    @Test
    fun whenServiceIsCreated_shouldContainsGenesisBlock() {
        val blockchain = objectUnderTest.getBlockchain()
        assert(blockchain.chain.size == 1)
        assert(blockchain.pendingTransactions.isEmpty())
        assert(blockchain.chain.first().hash == BlockchainConstants.DEFAULT_HASH)
        assert(blockchain.chain.first().previousBlockHash == BlockchainConstants.DEFAULT_HASH)
        assert(blockchain.chain.first().transactions.isEmpty())
        assert(blockchain.chain.first().index == 0)
        assert(blockchain.chain.first().nonce == BlockchainConstants.DEFAULT_NONCE)
    }

    @Test
    fun whenAddingBroadcastedTransaction_shouldAddItToPendingTransactions() {
        val broadcastedTransaction = BroadcastedTransaction(AMOUNT, SENDER, RECIPIENT, TRANSACTION_ID)
        val transaction = objectUnderTest.addBroadcastedTransaction(broadcastedTransaction)
        assert(transaction.amount == AMOUNT)
        assert(transaction.blockIndex == BLOCK_INDEX)
        assert(transaction.recipient == RECIPIENT)
        assert(transaction.sender == SENDER)
        assert(transaction.transactionId == TRANSACTION_ID)

        val blockchain = objectUnderTest.getBlockchain()
        assert(blockchain.pendingTransactions.size == 1)
        assert(blockchain.pendingTransactions.first().amount == transaction.amount)
        assert(blockchain.pendingTransactions.first().blockIndex == transaction.blockIndex)
        assert(blockchain.pendingTransactions.first().recipient == transaction.recipient)
        assert(blockchain.pendingTransactions.first().sender == transaction.sender)
        assert(blockchain.pendingTransactions.first().transactionId == transaction.transactionId)
    }

    @Test
    fun whenCreatingAndBroadcastingTransaction_shouldAddItToPendingTransactions() {
        val newTransaction = NewTransaction(AMOUNT, SENDER, RECIPIENT)
        val transaction = objectUnderTest.createAndBroadcastTransaction(newTransaction)
        assert(transaction.amount == AMOUNT)
        assert(transaction.blockIndex == BLOCK_INDEX)
        assert(transaction.recipient == RECIPIENT)
        assert(transaction.sender == SENDER)

        val blockchain = objectUnderTest.getBlockchain()
        assert(blockchain.pendingTransactions.size == 1)
        assert(blockchain.pendingTransactions.first().amount == transaction.amount)
        assert(blockchain.pendingTransactions.first().blockIndex == transaction.blockIndex)
        assert(blockchain.pendingTransactions.first().recipient == transaction.recipient)
        assert(blockchain.pendingTransactions.first().sender == transaction.sender)
        assert(blockchain.pendingTransactions.first().transactionId == transaction.transactionId)
    }

    @Test
    fun whenMining_shouldGenerateProofOfWorkCorrectly() {
        val newTransaction = NewTransaction(AMOUNT, SENDER, RECIPIENT)
        objectUnderTest.createAndBroadcastTransaction(newTransaction)
        objectUnderTest.mine()
        val blockchain = objectUnderTest.getBlockchain()
        val hash = blockchain.chain.last().generateHash()
        assert(blockchain.chain.last().hash == hash)
    }

    @Test
    fun whenMining_shouldAddPendingTransactionsToTheNewBlock() {
        val newTransaction = NewTransaction(AMOUNT, SENDER, RECIPIENT)
        objectUnderTest.createAndBroadcastTransaction(newTransaction)
        objectUnderTest.mine()
        val blockchain = objectUnderTest.getBlockchain()
        assert(blockchain.chain.last().transactions.first().amount == AMOUNT)
        assert(blockchain.chain.last().transactions.first().blockIndex == BLOCK_INDEX)
        assert(blockchain.chain.last().transactions.first().recipient == RECIPIENT)
        assert(blockchain.chain.last().transactions.first().sender == SENDER)
    }

    @Test
    fun whenRunningConsensus_shouldUpdateCurrentBlockchain() {
        objectUnderTest.mine()
        val newTransaction = NewTransaction(AMOUNT, SENDER, RECIPIENT)
        objectUnderTest.createAndBroadcastTransaction(newTransaction)
        objectUnderTest.mine()

        val blockchain = objectUnderTest.getBlockchain()
        val node = Node(DUMMY_NODE, blockchain)
        val newBlockchainService = BlockchainService(NetworkService(), BlockchainRepository())
        newBlockchainService.consensus(node)

        val currentBlockchain = newBlockchainService.getBlockchain()
        assert(currentBlockchain.chain.size == 3)
        assert(currentBlockchain.chain.last().transactions.size == 2)
        assert(currentBlockchain.chain.last().transactions.last().amount == AMOUNT)
        assert(currentBlockchain.chain.last().transactions.last().blockIndex == 2)
        assert(currentBlockchain.chain.last().transactions.last().recipient == RECIPIENT)
        assert(currentBlockchain.chain.last().transactions.last().sender == SENDER)
    }
}