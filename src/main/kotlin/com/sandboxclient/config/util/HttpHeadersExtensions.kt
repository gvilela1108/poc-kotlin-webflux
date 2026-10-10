package com.sandboxclient.config.util

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_XML

fun HttpHeaders?.acceptsXml(): Boolean = this?.accept?.any { it.isCompatibleWith(APPLICATION_XML) } == true

fun HttpHeaders?.acceptsCsv(): Boolean = this?.accept?.any { it.isCompatibleWith(MediaType.valueOf("text/csv")) } == true
