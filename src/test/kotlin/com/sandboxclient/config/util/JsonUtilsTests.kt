package com.sandboxclient.config.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JsonUtilsTests {
    @Test
    fun `returns null for null input`() {
        assertNull(JsonUtils.toJson(null))
    }

    @Test
    fun `serializes ordinary values as JSON`() {
        assertEquals("{\"name\":\"store\"}", JsonUtils.toJson(mapOf("name" to "store")))
    }

    @Test
    fun `truncates large serialized payload`() {
        val result = JsonUtils.toJson(mapOf("payload" to "x".repeat(2_500)))

        assertEquals(2_014, result!!.length)
        assertTrue(result.endsWith("...[truncated]"))
    }

    @Test
    fun `uses fallback when Jackson cannot serialize an object`() {
        val result = JsonUtils.toJson(UnsupportedValue())

        assertEquals("fallback-value", result)
    }

    @Test
    fun `returns unloggable marker when fallback toString fails`() {
        val result =
            JsonUtils.toJson(
                object {
                    val self: Any get() = this

                    override fun toString(): String = throw IllegalStateException("toString failed")
                },
            )

        assertEquals("[unloggable]", result)
    }

    private class UnsupportedValue {
        val self: UnsupportedValue get() = this

        override fun toString(): String = "fallback-value"
    }
}
