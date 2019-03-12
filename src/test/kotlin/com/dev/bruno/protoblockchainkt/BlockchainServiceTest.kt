package com.dev.bruno.protoblockchainkt

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
    private var objectUnderTest: BlockchainService? = null

    @Before
    fun setUp() {
        objectUnderTest = BlockchainService(NetworkService())
    }

    @Test
    fun whenServiceIsCreated_shouldContainsGenesisBlock() {
        val blockchain = objectUnderTest!!.getBlockchain().block()
        assert(blockchain!!.chain.size == 1)
        assert(blockchain.pendingTransactions.isEmpty())
        assert(blockchain.chain.first().hash == "0")
        assert(blockchain.chain.first().previousBlockHash == "0")
        assert(blockchain.chain.first().transactions.isEmpty())
        assert(blockchain.chain.first().index == 0)
        assert(blockchain.chain.first().nonce == 100L)
    }

    @Test
    fun whenCreatingTransaction_shouldAddItToPendingTransactions() {
        val transaction = objectUnderTest!!.createTransaction(AMOUNT, SENDER, RECIPIENT, TRANSACTION_ID).block()
        assert(transaction!!.amount == AMOUNT)
        assert(transaction.blockIndex == BLOCK_INDEX)
        assert(transaction.recipient == RECIPIENT)
        assert(transaction.sender == SENDER)
        assert(transaction.transactionId == TRANSACTION_ID)

        val blockchain = objectUnderTest!!.getBlockchain().block()
        assert(blockchain!!.pendingTransactions.size == 1)
        assert(blockchain.pendingTransactions.first().amount == transaction.amount)
        assert(blockchain.pendingTransactions.first().blockIndex == transaction.blockIndex)
        assert(blockchain.pendingTransactions.first().recipient == transaction.recipient)
        assert(blockchain.pendingTransactions.first().sender == transaction.sender)
        assert(blockchain.pendingTransactions.first().transactionId == transaction.transactionId)
    }

    @Test
    fun whenCreatingAndBroadcastingTransaction_shouldAddItToPendingTransactions() {
        val transaction = objectUnderTest!!.createAndBroadcastTransaction(AMOUNT, SENDER, RECIPIENT).block()
        assert(transaction!!.amount == AMOUNT)
        assert(transaction.blockIndex == BLOCK_INDEX)
        assert(transaction.recipient == RECIPIENT)
        assert(transaction.sender == SENDER)
        assert(transaction.transactionId != null)

        val blockchain = objectUnderTest!!.getBlockchain().block()
        assert(blockchain!!.pendingTransactions.size == 1)
        assert(blockchain.pendingTransactions.first().amount == transaction.amount)
        assert(blockchain.pendingTransactions.first().blockIndex == transaction.blockIndex)
        assert(blockchain.pendingTransactions.first().recipient == transaction.recipient)
        assert(blockchain.pendingTransactions.first().sender == transaction.sender)
        assert(blockchain.pendingTransactions.first().transactionId == transaction.transactionId)
    }
}