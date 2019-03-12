package com.dev.bruno.protoblockchainkt.api

import com.dev.bruno.protoblockchainkt.domain.Transaction
import com.dev.bruno.protoblockchainkt.dto.BroadcastedTransaction
import com.dev.bruno.protoblockchainkt.dto.NewTransaction
import com.dev.bruno.protoblockchainkt.service.BlockchainService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping(path = ["/transaction"])
class TransactionApi @Autowired constructor(val service: BlockchainService) {

    @RequestMapping(method = [RequestMethod.POST])
    fun createAndBroadcastTransaction(@RequestBody newTransaction: NewTransaction): Mono<Transaction> {
        return service.createAndBroadcastTransaction(newTransaction)
    }

    @RequestMapping(method = [RequestMethod.PUT])
    fun addBroadcastedTransaction(@RequestBody broadcastedTransaction: BroadcastedTransaction): Mono<Transaction> {
        return service.addBroadcastedTransaction(broadcastedTransaction)
    }
}