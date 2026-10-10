package com.sandboxclient.cache

import java.time.Duration

object CachePolicy {
    const val SNAPSHOT_KEY = "stores:snapshot"
    const val PAGE_KEY_PREFIX = "stores:page:"
    val ttl: Duration = Duration.ofMinutes(5)

    fun pageKey(
        page: Int,
        size: Int,
    ): String = "$PAGE_KEY_PREFIX$page:$size"
}
