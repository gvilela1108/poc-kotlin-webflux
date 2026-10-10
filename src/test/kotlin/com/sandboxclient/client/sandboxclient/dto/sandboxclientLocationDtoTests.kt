package com.sandboxclient.client.sandboxclient.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.sandboxclient.domain.OpeningHours
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class SandboxClientLocationDtoTests {
    @Test
    fun `maps all fields and nested opening hours to domain`() {
        val dto =
            SandboxClientLocationDto(
                7,
                "Store",
                "Berlin",
                "Main 7",
                "10115",
                listOf(SandboxClientOpeningHoursDto(false, 1, "09:00", "18:00", "", "")),
                listOf("market"),
                52.52,
                13.405,
            )
        val result = dto.toDomain()
        assertEquals(7, result.id)
        assertEquals("Store", result.name)
        assertEquals("Berlin", result.city)
        assertEquals("Main 7", result.streetAndNumber)
        assertEquals("10115", result.zip)
        assertEquals(listOf(OpeningHours(false, 1, "09:00", "18:00", "", "")), result.openingHours)
        assertEquals(listOf("market"), result.keywords)
        assertEquals(52.52, result.lat)
        assertEquals(13.405, result.lng)
    }

    @Test
    fun `keeps nullable fields and null opening hours`() {
        val result = SandboxClientLocationDto(1, "Store", "City", "Road", null, null, null, null, null).toDomain()
        assertNull(result.zip)
        assertNull(result.openingHours)
        assertNull(result.keywords)
        assertNull(result.lat)
        assertNull(result.lng)
    }

    @Test
    fun `supports default constructor and ignores unknown JSON fields`() {
        val mapper = ObjectMapper().findAndRegisterModules()
        val dto =
            mapper.readValue(
                """{"id":2,"name":"Shop","city":"Berlin","streetAndNumber":"Road","zip":null,"openingHours":[],"keywords":[],"lat":null,"lng":null,"unexpected":"ignored"}""",
                SandboxClientLocationDto::class.java,
            )
        assertEquals("Shop", dto.name)
        assertEquals(0, SandboxClientLocationDto().id)
    }
}
