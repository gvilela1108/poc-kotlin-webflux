package com.sandboxclient.domain

/** A page of stores in the application domain. */
data class StorePage(
    val count: Int,
    val locations: List<Location>,
)
