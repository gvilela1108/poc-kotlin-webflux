package com.sandboxclient.client.sandboxclient

import com.sandboxclient.client.sandboxclient.dto.SandboxClientLocationGetResponse
import com.sandboxclient.client.sandboxclient.dto.SandboxClientLocationsResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "sandboxclientApiClient",
    url = "\${sandboxclient.api.base-url}",
    configuration = [SandboxClientFeignConfig::class],
)
interface SandboxClientApiClient {
    @GetMapping(
        "/locations/{id}",
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun getLocation(
        @PathVariable("id") id: Long,
        @RequestHeader("privateKey") privateKey: String,
    ): SandboxClientLocationGetResponse

    @GetMapping(
        "/locations",
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun getLocations(
        @RequestHeader("privateKey") privateKey: String,
        @RequestParam("offset") offset: Int,
        @RequestParam("max") max: Int,
    ): SandboxClientLocationsResponse
}
