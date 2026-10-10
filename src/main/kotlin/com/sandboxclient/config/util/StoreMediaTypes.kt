package com.sandboxclient.config.util

import org.springframework.http.MediaType

object StoreMediaTypes {
    const val JSON_VALUE = "application/json"
    const val XML_VALUE = "application/xml"
    const val CSV_VALUE = "text/csv"

    val JSON: MediaType = MediaType.APPLICATION_JSON
    val XML: MediaType = MediaType.APPLICATION_XML
    val CSV: MediaType = MediaType("text", "csv")
}
