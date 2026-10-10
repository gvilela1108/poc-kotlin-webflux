package com.sandboxclient.config.util

import com.sandboxclient.controller.response.StorePageResponse
import com.sandboxclient.domain.Location
import org.springframework.http.MediaType

interface LocationFormatter {
    fun supports(mediaType: MediaType): Boolean

    fun mediaType(): MediaType

    fun format(page: StorePageResponse): Any

    fun format(location: Location): String
}
