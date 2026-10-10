package com.sandboxclient.config.util

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.sandboxclient.controller.response.LocationListResponse
import com.sandboxclient.controller.response.StoreLocationResponse
import com.sandboxclient.controller.response.StorePageResponse
import com.sandboxclient.domain.Location
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

@Component
class XmlLocationFormatter(
    private val mapper: XmlMapper,
) : LocationFormatter {
    override fun supports(mediaType: MediaType): Boolean = mediaType.isCompatibleWith(StoreMediaTypes.XML)

    override fun mediaType(): MediaType = StoreMediaTypes.XML

    override fun format(page: StorePageResponse): String =
        mapper.writeValueAsString(
            LocationListResponse(
                count = page.count,
                locations = page.locations,
            ),
        )

    override fun format(location: Location): String =
        mapper.writeValueAsString(
            StoreLocationResponse.fromDomain(location),
        )
}
