package com.sandboxclient.config.util

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.NotAcceptableStatusException

@Component
class LocationFormatterRegistry(
    private val formatters: List<LocationFormatter>,
) {
    fun resolve(accepted: List<MediaType>): LocationFormatter {
        if (accepted.isEmpty() || accepted.any { it == MediaType.ALL }) {
            return formatterFor(StoreMediaTypes.JSON)
        }

        return accepted
            .asSequence()
            .sortedByDescending(MediaType::getQualityValue)
            .flatMap { requested ->
                formatters
                    .asSequence()
                    .filter { it.supports(requested) }
            }.firstOrNull()
            ?: throw NotAcceptableStatusException(
                "Supported formats: application/json, application/xml, text/csv",
            )
    }

    private fun formatterFor(mediaType: MediaType): LocationFormatter =
        formatters.firstOrNull { it.supports(mediaType) }
            ?: error("No formatter registered for $mediaType")
}
