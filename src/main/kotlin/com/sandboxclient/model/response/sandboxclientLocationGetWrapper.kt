package com.sandboxclient.model.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.sandboxclient.model.Location

@JsonIgnoreProperties(ignoreUnknown = true)
data class SandboxClientLocationGetWrapper(
    val location: Location,
) {
    constructor() : this(
        Location(),
    )
}
