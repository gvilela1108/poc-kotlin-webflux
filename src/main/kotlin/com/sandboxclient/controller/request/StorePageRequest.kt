package com.sandboxclient.controller.request

import com.sandboxclient.domain.StorePageQuery
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class StorePageRequest(
    @field:Min(0, message = "page must be greater than or equal to 0")
    val page: Int = 0,
    @field:Min(1, message = "size must be greater than 0")
    @field:Max(1000, message = "size must not exceed 1000")
    val size: Int = 1000,
) {
    fun toDomain(): StorePageQuery = StorePageQuery(page = page, size = size)
}
