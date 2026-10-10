package com.sandboxclient.client.sandboxclient.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.sandboxclient.client.sandboxclient.dto.SandboxClientLocationDto as Location

@JsonIgnoreProperties(ignoreUnknown = true)
data class SandboxClientLocationGetWrapper(
    val location: Location,
) {
    constructor() : this(
        Location(),
    )
}
