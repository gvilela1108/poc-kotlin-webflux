package com.sandboxclient.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpeningHours(
    val closed: Boolean,
    val dayOfWeek: Int,
    val from1: String?,
    val to1: String?,
    val from2: String = "",
    val to2: String = "",
) {
    // Needed for XmlMapper flow
    constructor() : this(true, 0, "", "")
}
