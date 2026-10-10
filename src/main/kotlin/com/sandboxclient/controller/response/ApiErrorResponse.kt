package com.sandboxclient.controller.response

import java.time.OffsetDateTime

data class ApiErrorResponse(
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val status: Int,
    val code: String,
    val message: String,
    val correlationId: String,
)
