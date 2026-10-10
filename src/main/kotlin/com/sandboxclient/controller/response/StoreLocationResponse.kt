package com.sandboxclient.controller.response

import com.sandboxclient.domain.Location

/** Public API contract shared by JSON, CSV and XML consumers. */
data class StoreLocationResponse(
    val id: Long,
    val name: String,
    val city: String,
    val zip: String?,
    val streetAndNo: String,
    val openingHours: List<OpeningHoursResponse>,
    val keywords: List<String>,
    val lat: Double?,
    val lng: Double?,
) {
    companion object {
        fun fromDomain(location: Location): StoreLocationResponse =
            StoreLocationResponse(
                id = location.id,
                name = location.name,
                city = location.city,
                zip = location.zip,
                streetAndNo = location.streetAndNumber,
                openingHours = location.openingHours.orEmpty().map(OpeningHoursResponse::fromDomain),
                keywords = location.keywords.orEmpty(),
                lat = location.lat,
                lng = location.lng,
            )
    }
}
