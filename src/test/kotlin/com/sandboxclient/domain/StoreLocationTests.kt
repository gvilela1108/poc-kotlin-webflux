package com.sandboxclient.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StoreLocationTests {
    @Test
    fun `maps location fields and optional collections`() {
        val location =
            Location(
                id = 42,
                name = "Central Store",
                city = "Berlin",
                streetAndNumber = "Main Street 10",
                zip = "10115",
                openingHours = listOf(OpeningHours(false, 1, "09:00", "18:00")),
                keywords = listOf("grocery", "market"),
                lat = 52.52,
                lng = 13.405,
            )

        val result = StoreLocation.from(location)

        assertEquals("Central Store", result.name)
        assertEquals("Berlin", result.city)
        assertEquals("10115", result.zip)
        assertEquals("Main Street 10", result.streetAndNo)
        assertEquals(location.openingHours, result.openingHours)
        assertEquals(listOf("grocery", "market"), result.keywords)
        assertEquals(52.52, result.lat)
        assertEquals(13.405, result.lng)
    }

    @Test
    fun `replaces null collections with empty collections`() {
        val location = Location(1, "Store", "City", "Street", null, null, null, null, null)

        val result = StoreLocation.from(location)

        assertEquals(emptyList<OpeningHours>(), result.openingHours)
        assertEquals(emptyList<String>(), result.keywords)
        assertEquals(null, result.zip)
        assertEquals(null, result.lat)
        assertEquals(null, result.lng)
    }
}
