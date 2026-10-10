package com.sandboxclient.client.sandboxclient.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.sandboxclient.domain.OpeningHours

@JsonIgnoreProperties(ignoreUnknown = true)
data class SandboxClientOpeningHoursDto(
    val closed: Boolean = false,
    val dayOfWeek: Int = 0,
    val from1: String? = null,
    val to1: String? = null,
    val from2: String? = null,
    val to2: String? = null,
) {
    fun toDomain() =
        OpeningHours(
            closed = closed,
            dayOfWeek = dayOfWeek,
            from1 = from1,
            to1 = to1,
            from2 = from2 ?: "",
            to2 = to2 ?: "",
        )
}
