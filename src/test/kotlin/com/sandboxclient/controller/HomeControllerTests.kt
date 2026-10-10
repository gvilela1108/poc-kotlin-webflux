package com.sandboxclient.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HomeControllerTests {
    @Test
    fun `home endpoint returns ok`() {
        assertEquals("OK", HomeController().index())
    }
}
