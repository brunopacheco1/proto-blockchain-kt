package com.dev.bruno.protoblockchainkt

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableAutoConfiguration
@EnableWebFlux
class ProtoBlockchainKtApplication

fun main(args: Array<String>) {
    runApplication<ProtoBlockchainKtApplication>(*args)
}
