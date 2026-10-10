package com.sandboxclient.client.sandboxclient.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.sandboxclient.domain.Location

@JsonIgnoreProperties(ignoreUnknown = true)
data class SandboxClientLocationDto(
    val id: Long,
    val name: String,
    val city: String,
    val streetAndNumber: String,
    val zip: String?,
    val openingHours: List<SandboxClientOpeningHoursDto>?,
    val keywords: List<String>?,
    val lat: Double?,
    val lng: Double?,
) {
    constructor() : this(0, "", "", "", "", emptyList(), emptyList(), 0.0, 0.0)

    fun toDomain() =
        Location(
            id = id,
            name = name,
            city = city,
            streetAndNumber = streetAndNumber,
            zip = zip,
            openingHours = openingHours?.map(SandboxClientOpeningHoursDto::toDomain),
            keywords = keywords,
            lat = lat,
            lng = lng,
        )
}
