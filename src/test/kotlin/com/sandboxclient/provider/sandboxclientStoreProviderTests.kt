package com.sandboxclient.provider

import com.sandboxclient.client.sandboxclient.SandboxClientApiClient
import com.sandboxclient.client.sandboxclient.dto.SandboxClientLocationDto
import com.sandboxclient.client.sandboxclient.dto.SandboxClientLocationsResponse
import com.sandboxclient.client.sandboxclient.dto.SandboxClientLocationsWrapper
import com.sandboxclient.client.sandboxclient.dto.SandboxClientOpeningHoursDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`

class SandboxClientStoreProviderTests {
    private val client = mock(SandboxClientApiClient::class.java)
    private lateinit var provider: SandboxClientStoreProvider

    @BeforeEach
    fun setUp() {
        provider = SandboxClientStoreProvider(client, "test-private-key", 2)
    }

    @Test
    fun `fetches all pages and maps locations into domain`() {
        val first = dto(1, "First")
        val second = dto(2, "Second")
        val third = dto(3, "Third")
        `when`(client.getLocations("test-private-key", 0, 2))
            .thenReturn(response(3, listOf(first, second)))
        `when`(client.getLocations("test-private-key", 2, 2))
            .thenReturn(response(3, listOf(third)))

        val result = provider.findAll()

        assertEquals(listOf(1L, 2L, 3L), result.map { it.id })
        assertEquals("First", result.first().name)
        assertEquals("shop", result.first().keywords?.first())
        assertEquals(
            "09:00",
            result
                .first()
                .openingHours
                ?.first()
                ?.from1,
        )
        verify(client).getLocations("test-private-key", 0, 2)
        verify(client).getLocations("test-private-key", 2, 2)
        verifyNoMoreInteractions(client)
    }

    @Test
    fun `returns empty list when API has no locations`() {
        `when`(client.getLocations("test-private-key", 0, 2)).thenReturn(response(0, emptyList()))

        assertEquals(emptyList<com.sandboxclient.domain.Location>(), provider.findAll())
        verify(client).getLocations("test-private-key", 0, 2)
        verifyNoMoreInteractions(client)
    }

    @Test
    fun `stops when API returns an empty page before reported total`() {
        `when`(client.getLocations("test-private-key", 0, 2)).thenReturn(response(10, listOf(dto(1, "One"))))
        `when`(client.getLocations("test-private-key", 1, 2)).thenReturn(response(10, emptyList()))

        assertEquals(listOf(1L), provider.findAll().map { it.id })
        verify(client).getLocations("test-private-key", 0, 2)
        verify(client).getLocations("test-private-key", 1, 2)
    }

    @Test
    fun `maps nullable optional fields without failing`() {
        val location =
            dto(4, "Optional fields").copy(
                zip = null,
                openingHours = null,
                keywords = null,
                lat = null,
                lng = null,
            )
        `when`(client.getLocations("test-private-key", 0, 2)).thenReturn(response(1, listOf(location)))

        val result = provider.findAll().single()

        assertEquals(null, result.zip)
        assertEquals(null, result.openingHours)
        assertEquals(null, result.keywords)
        assertEquals(null, result.lat)
        assertEquals(null, result.lng)
    }

    private fun response(
        count: Int,
        locations: List<SandboxClientLocationDto>,
    ) = SandboxClientLocationsResponse(response = SandboxClientLocationsWrapper(count = count, locations = locations))

    private fun dto(
        id: Long,
        name: String,
    ) = SandboxClientLocationDto(
        id = id,
        name = name,
        city = "Berlin",
        streetAndNumber = "Main Street $id",
        zip = "10115",
        openingHours = listOf(SandboxClientOpeningHoursDto(dayOfWeek = 1, from1 = "09:00", to1 = "18:00")),
        keywords = listOf("shop"),
        lat = 52.52,
        lng = 13.405,
    )
}
