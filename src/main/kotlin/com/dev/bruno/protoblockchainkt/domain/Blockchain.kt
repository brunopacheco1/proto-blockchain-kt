package com.dev.bruno.protoblockchainkt.domain

import com.dev.bruno.protoblockchainkt.helper.generateHash

data class Blockchain(
        val chain: LinkedHashSet<Block> = linkedSetOf(),
        val pendingTransactions: ArrayList<Transaction> = arrayListOf()
) {

    fun isValid(): Boolean {
        val chain = chain.toList()
        val hasValidHashChain = chain.mapIndexed { index, block ->
            index == 0 || (chain[index - 1].hash == block.previousBlockHash && block.generateHash() == block.hash && block.hash.startsWith(BlockchainConstants.HASH_PREFIX))
        }.all { it }
        val hasValidGenesisBlock = chain[0].hash == BlockchainConstants.DEFAULT_HASH && chain[0].previousBlockHash == BlockchainConstants.DEFAULT_HASH &&
                chain[0].nonce == BlockchainConstants.DEFAULT_NONCE && chain[0].transactions.isEmpty() && chain[0].index == 0
        return hasValidHashChain && hasValidGenesisBlock
    }
}