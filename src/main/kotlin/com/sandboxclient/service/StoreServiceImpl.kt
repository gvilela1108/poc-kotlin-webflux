package com.sandboxclient.service

import com.sandboxclient.cache.StoreCacheService
import com.sandboxclient.config.log.LogMessagesEnum.STORE_FETCH_ALL_RESULT
import com.sandboxclient.config.log.LogMessagesEnum.STORE_FETCH_ALL_STARTED
import com.sandboxclient.config.log.LogMessagesEnum.STORE_FETCH_BY_ID_RESULT
import com.sandboxclient.config.log.LogMessagesEnum.STORE_FETCH_BY_ID_STARTED
import com.sandboxclient.config.log.LogMessagesEnum.STORE_FETCH_FAILED
import com.sandboxclient.config.log.LogMessagesEnum.STORE_SNAPSHOT_REFRESHED
import com.sandboxclient.config.log.LoggerHandler
import com.sandboxclient.domain.Location
import com.sandboxclient.domain.StorePage
import com.sandboxclient.domain.StorePageQuery
import com.sandboxclient.repository.StoreRepository
import com.sandboxclient.synchronization.StoreSynchronizationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class StoreServiceImpl(
    private val synchronizationService: StoreSynchronizationService,
    private val repository: StoreRepository,
    private val cache: StoreCacheService,
) : StoreService {
    override fun findAll(query: StorePageQuery): Mono<StorePage> =
        Mono
            .fromCallable {
                LoggerHandler.info(log, STORE_FETCH_ALL_STARTED)
                findPage(query).also { LoggerHandler.info(log, STORE_FETCH_ALL_RESULT) }
            }.doOnError { LoggerHandler.error(log, STORE_FETCH_FAILED, it) }
            .subscribeOn(Schedulers.boundedElastic())

    override fun findById(id: Long): Mono<Location> =
        Mono
            .fromCallable {
                LoggerHandler.info(log, STORE_FETCH_BY_ID_STARTED, id)
                ensureFreshSnapshot()
                repository.findById(id)
            }.flatMap { location ->
                if (location != null) {
                    LoggerHandler.info(log, STORE_FETCH_BY_ID_RESULT, id)
                }
                Mono.justOrEmpty(location)
            }.doOnError { exception -> LoggerHandler.error(log, STORE_FETCH_FAILED, exception) }
            .subscribeOn(Schedulers.boundedElastic())

    private fun findPage(query: StorePageQuery): StorePage {
        cache.getPage(query)?.let { return it }

        ensureFreshSnapshot()
        return repository.findPage(query.page, query.size).also { cache.savePage(query, it) }
    }

    private fun ensureFreshSnapshot() {
        if (cache.hasFreshSnapshot()) return

        if (!synchronizationService.synchronize()) return

        cache.clearPages()
        cache.markSnapshotFresh()
        LoggerHandler.info(log, STORE_SNAPSHOT_REFRESHED)
    }

    private companion object {
        val log = LoggerFactory.getLogger(StoreServiceImpl::class.java)
    }
}
