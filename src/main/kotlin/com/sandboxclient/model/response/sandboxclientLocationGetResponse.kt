package com.sandboxclient.model.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SandboxClientLocationGetResponse(
    val status: String,
    val response: SandboxClientLocationGetWrapper,
) {
    constructor() : this("", SandboxClientLocationGetWrapper())
}
