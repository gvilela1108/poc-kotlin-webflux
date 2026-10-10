package com.sandboxclient.config.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.sandboxclient.controller.response.StoreLocationResponse
import com.sandboxclient.controller.response.StorePageResponse
import com.sandboxclient.domain.Location
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

@Component
class CsvLocationFormatter(
    private val objectMapper: ObjectMapper,
) : LocationFormatter {
    override fun supports(mediaType: MediaType): Boolean = mediaType.isCompatibleWith(StoreMediaTypes.CSV)

    override fun mediaType(): MediaType = StoreMediaTypes.CSV

    override fun format(page: StorePageResponse): String = formatRows(page.locations)

    override fun format(location: Location): String = formatRows(listOf(StoreLocationResponse.fromDomain(location)))

    private fun formatRows(locations: List<StoreLocationResponse>): String =
        buildString {
            appendLine(HEADER)
            locations.forEach { location ->
                appendLine(toCsvRow(location))
            }
        }

    private fun toCsvRow(location: StoreLocationResponse): String =
        listOf(
            location.name,
            location.city,
            location.zip.orEmpty(),
            location.streetAndNo,
            serialize(location.openingHours),
            serialize(location.keywords),
            location.lat?.toString().orEmpty(),
            location.lng?.toString().orEmpty(),
        ).joinToString(DELIMITER, transform = ::escapeCsv)

    private fun serialize(value: Any): String = objectMapper.writeValueAsString(value)

    private fun escapeCsv(value: String): String =
        if (value.any { it in CSV_SPECIAL_CHARACTERS }) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }

    private companion object {
        const val HEADER = "name,city,zip,streetAndNo,openingHours,keywords,lat,lng"
        const val DELIMITER = ","
        val CSV_SPECIAL_CHARACTERS = setOf(',', '"', '\n', '\r')
    }
}
