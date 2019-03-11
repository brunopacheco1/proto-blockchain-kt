package com.dev.bruno.protoblockchainkt.api

import com.dev.bruno.protoblockchainkt.domain.Transaction
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
    fun createTransaction(@RequestBody transaction: Transaction): Mono<Transaction> {
        val (_, amount, sender, recipient) = transaction
        return service.createAndBroadcastTransaction(amount, recipient, recipient)
    }

    @RequestMapping(method = [RequestMethod.PUT])
    fun addBroadcastedTransaction(@RequestBody transaction: Transaction): Mono<Transaction> {
        val (_, amount, sender, recipient, transactionId) = transaction
        return service.createTransaction(amount, sender, recipient, transactionId)
    }
}