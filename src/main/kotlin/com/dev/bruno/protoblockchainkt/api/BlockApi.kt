package com.dev.bruno.protoblockchainkt.api

import com.dev.bruno.protoblockchainkt.domain.Block
import com.dev.bruno.protoblockchainkt.service.BlockchainService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/block"])
class BlockApi @Autowired constructor(val service: BlockchainService) {

    @RequestMapping(path = ["/mine"], method = [RequestMethod.POST])
    fun mine(): Block {
        return service.mine()
    }
}