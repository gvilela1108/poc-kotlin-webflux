package com.sandboxclient.client.sandboxclient.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.sandboxclient.client.sandboxclient.dto.SandboxClientLocationDto as Location

@JsonIgnoreProperties(ignoreUnknown = true)
data class SandboxClientLocationsResponse(
    val status: String = "",
    val response: SandboxClientLocationsWrapper = SandboxClientLocationsWrapper(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SandboxClientLocationsWrapper(
    val offset: Int = 0,
    val max: Int = 0,
    val count: Int = 0,
    val locations: List<Location> = emptyList(),
)
