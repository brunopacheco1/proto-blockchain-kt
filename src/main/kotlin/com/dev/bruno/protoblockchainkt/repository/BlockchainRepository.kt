package com.dev.bruno.protoblockchainkt.repository

import com.dev.bruno.protoblockchainkt.domain.Blockchain
import org.springframework.stereotype.Repository

@Repository
class BlockchainRepository {

    private var blockchain = Blockchain()

    fun getBlockchain() = this.blockchain

    fun setBlockchain(blockchain: Blockchain) {
        this.blockchain = blockchain
    }
}