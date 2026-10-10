package com.sandboxclient.cache

import com.sandboxclient.domain.StorePage
import com.sandboxclient.domain.StorePageQuery

interface StoreCacheService {
    fun getPage(query: StorePageQuery): StorePage?

    fun savePage(
        query: StorePageQuery,
        page: StorePage,
    )

    fun hasFreshSnapshot(): Boolean

    fun clearPages()

    fun markSnapshotFresh()
}
