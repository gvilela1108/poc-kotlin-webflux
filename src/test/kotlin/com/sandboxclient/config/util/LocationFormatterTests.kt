package com.sandboxclient.config.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class LocationFormatterTests {
    @Test
    fun `JSON formatter supports JSON media type`() {
        val formatter =
            JsonLocationFormatter(
                ObjectMapper().findAndRegisterModules(),
            )

        assertTrue(formatter.supports(MediaType.APPLICATION_JSON))
        assertFalse(formatter.supports(MediaType.APPLICATION_XML))
    }

    @Test
    fun `XML formatter supports XML media type`() {
        val xmlMapper: XmlMapper = XmlMapper()
        val formatter = XmlLocationFormatter(mapper = xmlMapper)

        assertTrue(formatter.supports(MediaType.APPLICATION_XML))
        assertFalse(formatter.supports(MediaType.APPLICATION_JSON))
    }
}
