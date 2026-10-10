package com.sandboxclient.support

import com.sandboxclient.domain.Location
import com.sandboxclient.domain.OpeningHours
import com.sandboxclient.domain.StorePage
import com.sandboxclient.domain.StorePageQuery

/** Shared test fixtures; keep defaults representative and override only relevant fields. */
object TestDataFactory {
    fun location(
        id: Long = 1L,
        name: String = "Test Store",
        city: String = "Berlin",
        streetAndNumber: String = "Main Street 1",
        zip: String? = "10115",
        openingHours: List<OpeningHours>? = listOf(OpeningHours(false, 1, "09:00", "18:00")),
        keywords: List<String>? = listOf("shop"),
        lat: Double? = 52.52,
        lng: Double? = 13.405,
    ) = Location(id, name, city, streetAndNumber, zip, openingHours, keywords, lat, lng)

    fun page(locations: List<Location> = listOf(location())) = StorePage(locations.size, locations)

    fun query(
        page: Int = 0,
        size: Int = 20,
    ) = StorePageQuery(page, size)
}
