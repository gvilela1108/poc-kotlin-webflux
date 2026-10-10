package com.sandboxclient.config.util

import org.junit.jupiter.api.Test

class ApplicationListenerTests {
    private val listener = ApplicationListener()

    @Test
    fun `handles application ready event`() {
        listener.onApplicationReady()
    }

    @Test
    fun `handles application shutdown`() {
        listener.onShutdown()
    }
}
