package com.sandboxclient.repository.entities

import com.sandboxclient.domain.Location
import com.sandboxclient.domain.OpeningHours
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StoreLocationEntityTests {
    @Test
    fun `maps location to entity and back to domain`() {
        val source =
            Location(
                id = 12,
                name = "Store",
                city = "Berlin",
                streetAndNumber = "Street 1",
                zip = "10115",
                openingHours = listOf(OpeningHours(false, 1, "09:00", "17:00", "18:00", "20:00")),
                keywords = listOf("food", "local"),
                lat = 52.5,
                lng = 13.4,
            )

        val entity = StoreLocationEntity(source)
        val result = entity.toDomain()

        assertEquals(source, result)
        assertEquals(source.id, entity.id)
        assertEquals(source.name, entity.name)
        assertEquals(source.keywords, entity.keywords)
    }

    @Test
    fun `uses empty collections for null domain collections`() {
        val entity = StoreLocationEntity(Location(1, "Store", "City", "Road", null, null, null, null, null))

        assertTrue(entity.openingHours.isEmpty())
        assertTrue(entity.keywords.isEmpty())
        assertEquals(emptyList<OpeningHours>(), entity.toDomain().openingHours)
        assertEquals(emptyList<String>(), entity.toDomain().keywords)
    }

    @Test
    fun `entity equality and hash code are based on id`() {
        val first = StoreLocationEntity(Location(5, "A", "City", "Road", null, null, null, null, null))
        val sameId = StoreLocationEntity(Location(5, "B", "Elsewhere", "Street", null, null, null, null, null))
        val differentId = StoreLocationEntity(Location(6, "A", "City", "Road", null, null, null, null, null))

        assertTrue(first == first)
        assertTrue(first == sameId)
        assertFalse(first == differentId)
        assertFalse(first == null)
        assertNotEquals(first.hashCode(), differentId.hashCode())
    }
}
