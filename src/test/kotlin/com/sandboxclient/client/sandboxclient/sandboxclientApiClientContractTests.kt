package com.sandboxclient.client.sandboxclient

import com.sandboxclient.client.sandboxclient.dto.SandboxClientLocationDto
import com.sandboxclient.client.sandboxclient.dto.SandboxClientLocationGetResponse
import com.sandboxclient.client.sandboxclient.dto.SandboxClientLocationsResponse
import com.sandboxclient.client.sandboxclient.dto.SandboxClientOpeningHoursDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

class SandboxClientApiClientContractTests {
    @Test
    fun `locations endpoint declares expected route and request parameters`() {
        val method =
            SandboxClientApiClient::class.java.getMethod(
                "getLocations",
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
            )
        assertEquals("/locations", method.getAnnotation(GetMapping::class.java).value.single())
        val parameters = method.parameterAnnotations
        assertTrue(parameters[0].filterIsInstance<RequestHeader>().single().value == "privateKey")
        assertTrue(parameters[1].filterIsInstance<RequestParam>().single().value == "offset")
        assertTrue(parameters[2].filterIsInstance<RequestParam>().single().value == "max")
    }

    @Test
    fun `single location endpoint declares id path and private key header`() {
        val method = SandboxClientApiClient::class.java.getMethod("getLocation", Long::class.javaPrimitiveType, String::class.java)
        assertEquals("/locations/{id}", method.getAnnotation(GetMapping::class.java).value.single())
        assertEquals(
            "id",
            method.parameterAnnotations[0]
                .filterIsInstance<PathVariable>()
                .single()
                .value,
        )
        assertEquals(
            "privateKey",
            method.parameterAnnotations[1]
                .filterIsInstance<RequestHeader>()
                .single()
                .value,
        )
    }

    @Test
    fun `location DTO maps all fields and nullable optional values`() {
        val dto =
            SandboxClientLocationDto(
                id = 9,
                name = "DTO Store",
                city = "Berlin",
                streetAndNumber = "Street 9",
                zip = null,
                openingHours = listOf(SandboxClientOpeningHoursDto(closed = true, dayOfWeek = 6, from1 = null, to1 = null)),
                keywords = null,
                lat = null,
                lng = null,
            )
        val domain = dto.toDomain()
        assertEquals(9L, domain.id)
        assertEquals("DTO Store", domain.name)
        assertEquals(null, domain.zip)
        assertEquals(true, domain.openingHours?.single()?.closed)
        assertEquals("", domain.openingHours?.single()?.from2)
        assertEquals(null, domain.keywords)
        assertEquals(null, domain.lat)
    }

    @Test
    fun `opening hours DTO converts nullable second interval to empty strings`() {
        val domain = SandboxClientOpeningHoursDto(dayOfWeek = 2, from1 = "10:00", to1 = "17:00").toDomain()
        assertEquals("10:00", domain.from1)
        assertEquals("17:00", domain.to1)
        assertEquals("", domain.from2)
        assertEquals("", domain.to2)
    }

    @Test
    fun `response DTOs provide defaults for absent fields`() {
        val listResponse = SandboxClientLocationsResponse()
        assertEquals("", listResponse.status)
        assertEquals(0, listResponse.response.count)
        assertTrue(listResponse.response.locations.isEmpty())

        val singleResponse = SandboxClientLocationGetResponse()
        assertEquals("", singleResponse.status)
        assertEquals(0L, singleResponse.response.location.id)
        assertEquals("", singleResponse.response.location.name)
    }
}
