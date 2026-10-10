package com.sandboxclient.config.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.sandboxclient.controller.response.LocationListResponse
import com.sandboxclient.controller.response.StoreLocationResponse
import com.sandboxclient.controller.response.StorePageResponse
import com.sandboxclient.domain.Location
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

@Component
class JsonLocationFormatter(
    private val objectMapper: ObjectMapper,
) : LocationFormatter {
    override fun supports(mediaType: MediaType): Boolean = mediaType.isCompatibleWith(StoreMediaTypes.JSON)

    override fun mediaType(): MediaType = StoreMediaTypes.JSON

    override fun format(page: StorePageResponse): String =
        objectMapper.writeValueAsString(
            LocationListResponse(
                count = page.count,
                locations = page.locations,
            ),
        )

    override fun format(location: Location): String =
        objectMapper.writeValueAsString(
            StoreLocationResponse.fromDomain(location),
        )
}
