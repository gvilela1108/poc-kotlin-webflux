package com.sandboxclient.controller.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class LocationListResponse(
    val count: Int,
    val locations: List<StoreLocationResponse>,
) {
    constructor() : this(0, emptyList())
}
