package com.sandboxclient.util

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_XML
import org.springframework.http.MediaType.TEXT_PLAIN

fun HttpHeaders?.acceptsXml(): Boolean = this?.accept?.contains(APPLICATION_XML) ?: false

fun HttpHeaders?.acceptsCsv(): Boolean = this?.accept?.contains(TEXT_PLAIN) ?: false
