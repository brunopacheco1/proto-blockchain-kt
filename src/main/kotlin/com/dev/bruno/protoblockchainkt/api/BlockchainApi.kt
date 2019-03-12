package com.dev.bruno.protoblockchainkt.api

import com.dev.bruno.protoblockchainkt.domain.Blockchain
import com.dev.bruno.protoblockchainkt.service.BlockchainService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/blockchain")
class BlockchainApi @Autowired constructor(private val blockchainService: BlockchainService){

    @RequestMapping
    fun getBlockchain(): Mono<Blockchain> {
        return blockchainService.getBlockchain()
    }
}