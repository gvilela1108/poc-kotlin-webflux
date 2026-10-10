
package com.sandboxclient.config.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.resource.NoResourceFoundException
import org.springframework.web.server.ServerWebInputException

class GlobalExceptionHandlerTests {
    private val handler = GlobalExceptionHandler()

    @Test
    fun `maps illegal argument to bad request and keeps message`() {
        val response =
            handler.handleBadRequest(
                IllegalArgumentException("invalid page"),
            )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("BAD_REQUEST", response.body!!.code)
        assertEquals("invalid page", response.body!!.message)
        assertNotNull(response.body!!.correlationId)
    }

    @Test
    fun `maps invalid request parameter to bad request`() {
        val response =
            handler.handleInvalidInput(
                ServerWebInputException("Type mismatch."),
            )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("BAD_REQUEST", response.body!!.code)
        assertEquals(
            "Invalid request parameter",
            response.body!!.message,
        )
        assertNotNull(response.body!!.correlationId)
    }

    @Test
    fun `maps external service exception to service unavailable`() {
        val response =
            handler.handleExternalService(
                ExternalServiceException("offline"),
            )

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.statusCode)
        assertEquals(
            "EXTERNAL_SERVICE_UNAVAILABLE",
            response.body!!.code,
        )
        assertEquals(
            "External service is temporarily unavailable",
            response.body!!.message,
        )
    }

    @Test
    fun `maps illegal argument without message to default bad request message`() {
        val response =
            handler.handleBadRequest(
                IllegalArgumentException(),
            )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid request", response.body!!.message)
    }

    @Test
    fun `maps missing resource to not found`() {
        val exception = NoResourceFoundException("/missing")

        val response = handler.handleResourceNotFound(exception)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("NOT_FOUND", response.body!!.code)
        assertEquals("Resource not found", response.body!!.message)
    }

    @Test
    fun `uses correlation id from MDC in error response`() {
        MDC.put("correlationId", "trace-abc")

        try {
            val response =
                handler.handleUnexpected(
                    IllegalStateException("internal detail"),
                )

            assertEquals("trace-abc", response.body!!.correlationId)
        } finally {
            MDC.remove("correlationId")
        }
    }

    @Test
    fun `maps not acceptable exception to empty 406`() {
        val response = handler.handleNotAcceptable()

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.statusCode)
        assertEquals(null, response.body)
    }

    @Test
    fun `maps unexpected error to internal error`() {
        val response =
            handler.handleUnexpected(
                IllegalStateException("secret"),
            )

        assertEquals(
            HttpStatus.INTERNAL_SERVER_ERROR,
            response.statusCode,
        )
        assertEquals("INTERNAL_ERROR", response.body!!.code)
        assertEquals(
            "An unexpected error occurred",
            response.body!!.message,
        )
    }
}
