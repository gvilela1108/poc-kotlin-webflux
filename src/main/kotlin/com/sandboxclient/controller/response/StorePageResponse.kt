package com.sandboxclient.controller.response

import com.sandboxclient.domain.StorePage

/** Public API representation of a page of stores. */
data class StorePageResponse(
    val count: Int,
    val locations: List<StoreLocationResponse>,
) {
    companion object {
        fun fromDomain(page: StorePage): StorePageResponse =
            StorePageResponse(
                count = page.count,
                locations = page.locations.map(StoreLocationResponse::fromDomain),
            )
    }
}
