package com.sandboxclient.config.log

import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class CorrelationIdFilter : WebFilter {
    companion object {
        const val HEADER = "X-Correlation-ID"
    }

    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> {
        val correlationId = exchange.request.headers.getFirst(HEADER) ?: UUID.randomUUID().toString()
        exchange.response.headers.add(HEADER, correlationId)
        return chain
            .filter(exchange)
            .contextWrite { it.put(HEADER, correlationId) }
            .doOnEach { signal ->
                if (signal.isOnNext || signal.isOnComplete) {
                    MDC.put("correlationId", correlationId)
                }
            }.doFinally { MDC.remove("correlationId") }
    }
}
