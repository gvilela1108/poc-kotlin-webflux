package com.sandboxclient.controller.response

import com.sandboxclient.domain.OpeningHours

/** Public API representation of a location's opening hours. */
data class OpeningHoursResponse(
    val closed: Boolean,
    val dayOfWeek: Int,
    val from1: String?,
    val to1: String?,
    val from2: String,
    val to2: String,
) {
    companion object {
        fun fromDomain(openingHours: OpeningHours): OpeningHoursResponse =
            OpeningHoursResponse(
                closed = openingHours.closed,
                dayOfWeek = openingHours.dayOfWeek,
                from1 = openingHours.from1,
                to1 = openingHours.to1,
                from2 = openingHours.from2,
                to2 = openingHours.to2,
            )
    }
}
