package com.sandboxclient.domain

/** Pagination criteria used by the application layer. */
data class StorePageQuery(
    val page: Int,
    val size: Int,
)
