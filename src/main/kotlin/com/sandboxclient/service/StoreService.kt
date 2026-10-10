package com.sandboxclient.service

import com.sandboxclient.domain.Location
import com.sandboxclient.domain.StorePage
import com.sandboxclient.domain.StorePageQuery
import reactor.core.publisher.Mono

interface StoreService {
    fun findAll(query: StorePageQuery): Mono<StorePage>

    fun findById(id: Long): Mono<Location>
}
