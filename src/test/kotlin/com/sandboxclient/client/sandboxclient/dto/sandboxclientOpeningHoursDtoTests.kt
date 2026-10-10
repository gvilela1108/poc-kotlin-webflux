package com.sandboxclient.client.sandboxclient.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SandboxClientOpeningHoursDtoTests {
    @Test
    fun `maps null second intervals to empty strings`() {
        val result = SandboxClientOpeningHoursDto(true, 7, null, null, null, null).toDomain()
        assertEquals(true, result.closed)
        assertEquals(7, result.dayOfWeek)
        assertEquals(null, result.from1)
        assertEquals(null, result.to1)
        assertEquals("", result.from2)
        assertEquals("", result.to2)
    }

    @Test
    fun `preserves supplied second intervals`() {
        val result = SandboxClientOpeningHoursDto(false, 2, "08:00", "12:00", "13:00", "17:00").toDomain()
        assertEquals("13:00", result.from2)
        assertEquals("17:00", result.to2)
    }
}
