package com.dev.bruno.protoblockchainkt

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
class ProtoBlockchainKtApplication

fun main(args: Array<String>) {
    runApplication<ProtoBlockchainKtApplication>(*args)
}
