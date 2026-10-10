package com.sandboxclient.domain

data class OpeningHours(
    val closed: Boolean,
    val dayOfWeek: Int,
    val from1: String?,
    val to1: String?,
    val from2: String = "",
    val to2: String = "",
) {
    constructor() : this(true, 0, "", "")
}
