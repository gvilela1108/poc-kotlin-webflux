package com.sandboxclient.domain

/** Public contract shared by JSON, CSV and XML consumers. */
data class StoreLocation(
    val name: String,
    val city: String,
    val zip: String?,
    val streetAndNo: String,
    val openingHours: List<OpeningHours>,
    val keywords: List<String>,
    val lat: Double?,
    val lng: Double?,
) {
    companion object {
        fun from(location: Location): StoreLocation =
            StoreLocation(
                name = location.name,
                city = location.city,
                zip = location.zip,
                streetAndNo = location.streetAndNumber,
                openingHours = location.openingHours.orEmpty(),
                keywords = location.keywords.orEmpty(),
                lat = location.lat,
                lng = location.lng,
            )
    }
}
