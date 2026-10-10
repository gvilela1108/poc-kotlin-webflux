package com.sandboxclient.model.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.sandboxclient.model.Location

@JsonIgnoreProperties(ignoreUnknown = true)
data class LocationListResponse(
    val count: Int,
    val locations: List<Location>,
) {
    constructor() : this(0, listOf())
}
