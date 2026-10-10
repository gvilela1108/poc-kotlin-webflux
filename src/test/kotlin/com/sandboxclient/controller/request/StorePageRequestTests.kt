package com.sandboxclient.controller.request

import com.sandboxclient.domain.StorePageQuery
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StorePageRequestTests {
    @Test
    fun `uses documented defaults`() {
        val request = StorePageRequest()

        assertEquals(0, request.page)
        assertEquals(1000, request.size)
        assertEquals(StorePageQuery(page = 0, size = 1000), request.toDomain())
    }

    @Test
    fun `maps provided page and size to domain query`() {
        assertEquals(
            StorePageQuery(page = 3, size = 25),
            StorePageRequest(page = 3, size = 25).toDomain(),
        )
    }
}
