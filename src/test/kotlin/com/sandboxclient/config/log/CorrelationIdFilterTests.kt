package com.sandboxclient.config.log

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

class CorrelationIdFilterTests {
    private val filter = CorrelationIdFilter()

    @Test
    fun `preserves incoming correlation id in response and reactor context`() {
        val id = "request-correlation-123"
        val exchange =
            MockServerWebExchange.from(
                MockServerHttpRequest.get("/stores").header(CorrelationIdFilter.HEADER, id).build(),
            )
        val chain =
            WebFilterChain {
                Mono.deferContextual { context ->
                    assertEquals(id, context.get<String>(CorrelationIdFilter.HEADER))
                    Mono.empty()
                }
            }

        filter.filter(exchange, chain).block()

        assertEquals(id, exchange.response.headers.getFirst(CorrelationIdFilter.HEADER))
        assertNull(MDC.get("correlationId"))
    }

    @Test
    fun `generates correlation id when request header is absent`() {
        val exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/stores").build())
        val chain = WebFilterChain { Mono.empty() }

        filter.filter(exchange, chain).block()

        val responseId = exchange.response.headers.getFirst(CorrelationIdFilter.HEADER)
        assertNotNull(responseId)
        assertEquals(responseId, UUID.fromString(responseId).toString())
        assertNull(MDC.get("correlationId"))
    }

    @Test
    fun `removes correlation id from MDC when chain terminates with error`() {
        val exchange =
            MockServerWebExchange.from(
                MockServerHttpRequest.get("/stores").header(CorrelationIdFilter.HEADER, "error-id").build(),
            )
        val chain = WebFilterChain { Mono.error(IllegalStateException("failure")) }

        runCatching { filter.filter(exchange, chain).block() }

        assertNull(MDC.get("correlationId"))
        assertTrue(exchange.response.headers.getFirst(CorrelationIdFilter.HEADER) == "error-id")
    }
}
