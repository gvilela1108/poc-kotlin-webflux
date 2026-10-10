package com.sandboxclient.config.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

class HttpHeadersExtensionsTests {
    @Test
    fun `handles null headers`() {
        assertFalse(null.acceptsXml())
        assertFalse(null.acceptsCsv())
    }

    @Test
    fun `detects compatible XML and CSV media types`() {
        val xml = HttpHeaders().apply { accept = listOf(MediaType.APPLICATION_XML) }
        val csv = HttpHeaders().apply { accept = listOf(MediaType.valueOf("text/csv")) }
        val json = HttpHeaders().apply { accept = listOf(MediaType.APPLICATION_JSON) }
        assertTrue(xml.acceptsXml())
        assertFalse(xml.acceptsCsv())
        assertTrue(csv.acceptsCsv())
        assertFalse(csv.acceptsXml())
        assertFalse(json.acceptsXml())
        assertFalse(json.acceptsCsv())
    }
}
