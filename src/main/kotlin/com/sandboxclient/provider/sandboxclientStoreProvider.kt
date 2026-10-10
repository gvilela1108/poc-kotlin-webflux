package com.sandboxclient.provider

import com.sandboxclient.client.sandboxclient.SandboxClientApiClient
import com.sandboxclient.config.exception.ExternalServiceException
import com.sandboxclient.config.log.LogMessagesEnum.EXTERNAL_REQUEST_FAILED
import com.sandboxclient.config.log.LogMessagesEnum.SANDBOXCLIENT_FETCH_COMPLETED
import com.sandboxclient.config.log.LogMessagesEnum.SANDBOXCLIENT_FETCH_STARTED
import com.sandboxclient.config.log.LogMessagesEnum.SANDBOXCLIENT_PAGE_FETCHED
import com.sandboxclient.config.log.LoggerHandler
import com.sandboxclient.domain.Location
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SandboxClientStoreProvider(
    private val client: SandboxClientApiClient,
    @Value("\${sandboxclient.api.private-key}")
    private val privateKey: String,
    @Value("\${sandboxclient.api.page-size:100}")
    private val pageSize: Int,
) : StoreProvider {
    @CircuitBreaker(
        name = "sandboxclient",
        fallbackMethod = "fallback",
    )
    override fun findAll(): List<Location> {
        LoggerHandler.info(
            log,
            SANDBOXCLIENT_FETCH_STARTED,
            mapOf("pageSize" to pageSize),
        )

        val locations = mutableListOf<Location>()
        var offset = 0
        var total = Int.MAX_VALUE
        var pages = 0

        while (offset < total) {
            val page =
                client
                    .getLocations(privateKey, offset, pageSize)
                    .response

            total = page.count
            val pageLocations = page.locations.map { it.toDomain() }

            locations += pageLocations
            pages++

            LoggerHandler.info(
                log,
                SANDBOXCLIENT_PAGE_FETCHED,
                mapOf(
                    "offset" to offset,
                    "pageSize" to pageSize,
                    "returned" to pageLocations.size,
                    "total" to total,
                ),
            )

            if (page.locations.isEmpty()) {
                break
            }

            offset += page.locations.size
        }

        LoggerHandler.info(
            log,
            SANDBOXCLIENT_FETCH_COMPLETED,
            mapOf(
                "totalLocations" to locations.size,
                "pages" to pages,
            ),
        )

        return locations
    }

    @Suppress("UNUSED_PARAMETER")
    private fun fallback(throwable: Throwable): List<Location> {
        LoggerHandler.error(
            log,
            EXTERNAL_REQUEST_FAILED,
            throwable,
        )

        throw ExternalServiceException(
            "SandboxClient API is unavailable",
            throwable,
        )
    }

    private companion object {
        val log =
            LoggerFactory.getLogger(
                SandboxClientStoreProvider::class.java,
            )
    }
}
