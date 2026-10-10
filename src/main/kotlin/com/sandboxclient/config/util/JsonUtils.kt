package com.sandboxclient.config.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper

object JsonUtils {
    private val objectMapper = ObjectMapper()

    /**
     * Maximum character count for log payloads.
     */
    private const val MAX_LENGTH = 2_000

    /**
     * Safely serializes an object to JSON for logging.
     * Never propagates serialization exceptions and truncates large payloads.
     */
    fun toJson(value: Any?): String? {
        if (value == null) {
            return null
        }

        return try {
            truncate(objectMapper.writeValueAsString(value))
        } catch (ex: JsonProcessingException) {
            fallback(value)
        } catch (ex: Exception) {
            "[unserializable]"
        }
    }

    private fun truncate(value: String): String =
        if (value.length <= MAX_LENGTH) {
            value
        } else {
            value.take(MAX_LENGTH) + "...[truncated]"
        }

    private fun fallback(value: Any): String =
        try {
            truncate(value.toString())
        } catch (ex: Exception) {
            "[unloggable]"
        }
}
