package com.dev.bruno.protoblockchainkt.api

import com.dev.bruno.protoblockchainkt.domain.Network
import com.dev.bruno.protoblockchainkt.service.NetworkService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@RestController
@RequestMapping("/network")
class NetworkApi @Autowired constructor(private val networkService: NetworkService) {

    @RequestMapping(method = [RequestMethod.GET])
    fun getNetwork(): Mono<Network> {
        return networkService.getNetwork().toMono()
    }
}