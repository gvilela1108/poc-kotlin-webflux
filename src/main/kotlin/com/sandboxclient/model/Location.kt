package com.sandboxclient.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Location(
    val id: Long,
    val name: String,
    val city: String,
    val streetAndNumber: String,
    val zip: String?,
    val openingHours: List<OpeningHours>?,
    val keywords: List<String>?,
    val lat: Double?,
    val lng: Double?,
) {
    constructor() : this(0, "", "", "", "", emptyList(), emptyList(), 0.0, 0.0)

    fun validate(): Boolean = name.isNotEmpty() && city.isNotEmpty() && streetAndNumber.isNotEmpty()
}
