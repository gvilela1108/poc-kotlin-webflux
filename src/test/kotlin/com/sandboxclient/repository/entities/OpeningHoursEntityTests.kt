package com.sandboxclient.repository.entities

import com.sandboxclient.domain.OpeningHours
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OpeningHoursEntityTests {
    @Test
    fun `maps opening hours to entity and back`() {
        val source = OpeningHours(false, 2, "08:30", "12:30", "13:30", "17:00")

        val result = OpeningHoursEntity(source).toDomain()

        assertEquals(source, result)
    }

    @Test
    fun `preserves closed day and missing intervals`() {
        val source = OpeningHours(true, 7, null, null)

        val result = OpeningHoursEntity(source).toDomain()

        assertEquals(source, result)
    }
}
