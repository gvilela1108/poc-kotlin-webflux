package com.sandboxclient.config.swagger

import com.sandboxclient.config.OpenApiConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class OpenApiConfigTests {
    @Test
    fun `defines API title version and supported response formats`() {
        val api = OpenApiConfig().sandboxclientOpenAPI()

        assertNotNull(api.info)
        assertEquals("SandboxClient Stores API", api.info.title)
        assertEquals("v1", api.info.version)
        assertEquals(true, api.info.description.contains("JSON (application/json)"))
        assertEquals(true, api.info.description.contains("XML (application/xml)"))
        assertEquals(true, api.info.description.contains("CSV (text/csv)"))
    }
}
