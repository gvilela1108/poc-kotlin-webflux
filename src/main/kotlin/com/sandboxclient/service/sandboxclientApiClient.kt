package com.sandboxclient.service

import com.sandboxclient.model.response.SandboxClientLocationGetResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono

@HttpExchange("/locations")
interface SandboxClientApiClient {
    @GetExchange("/{id}")
    fun getLocation(
        @PathVariable("id") id: Long,
        @RequestParam("private_key") privateKey: String,
    ): Mono<SandboxClientLocationGetResponse>
}
