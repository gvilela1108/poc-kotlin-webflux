package com.sandboxclient.config.util

import com.sandboxclient.controller.response.StorePageResponse
import com.sandboxclient.domain.Location
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.server.NotAcceptableStatusException

class LocationFormatterRegistryTests {
    private val json = StubFormatter(StoreMediaTypes.JSON)
    private val xml = StubFormatter(StoreMediaTypes.XML)
    private val csv = StubFormatter(StoreMediaTypes.CSV)
    private val registry = LocationFormatterRegistry(listOf(json, xml, csv))

    @Test
    fun `defaults to json when accept is empty`() {
        assertSame(json, registry.resolve(emptyList()))
    }

    @Test
    fun `defaults to json when accept contains wildcard`() {
        assertSame(json, registry.resolve(listOf(MediaType.ALL)))
    }

    @Test
    fun `chooses compatible formatter with highest quality`() {
        assertSame(
            xml,
            registry.resolve(listOf(MediaType.parseMediaType("text/csv;q=0.4"), MediaType.parseMediaType("application/xml;q=0.9"))),
        )
    }

    @Test
    fun `throws not acceptable when no formatter supports requested type`() {
        assertThrows(NotAcceptableStatusException::class.java) {
            registry.resolve(listOf(MediaType.APPLICATION_PDF))
        }
    }

    @Test
    fun `throws configuration error when json formatter is missing for wildcard`() {
        val emptyRegistry = LocationFormatterRegistry(emptyList())
        assertThrows(IllegalStateException::class.java) { emptyRegistry.resolve(emptyList()) }
    }

    private class StubFormatter(
        private val type: MediaType,
    ) : LocationFormatter {
        override fun supports(mediaType: MediaType) = mediaType.isCompatibleWith(type)

        override fun mediaType() = type

        override fun format(page: StorePageResponse): Any = "formatted page"

        override fun format(location: Location): String = "formatted location"
    }
}
