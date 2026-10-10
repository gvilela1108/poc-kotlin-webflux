package com.sandboxclient.service

import com.sandboxclient.cache.StoreCacheService
import com.sandboxclient.repository.StoreRepository
import com.sandboxclient.support.TestDataFactory
import com.sandboxclient.synchronization.StoreSynchronizationService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import reactor.test.StepVerifier

class StoreServiceImplTests {
    private val synchronizationService = mock(StoreSynchronizationService::class.java)
    private val repository = mock(StoreRepository::class.java)
    private val cache = mock(StoreCacheService::class.java)
    private lateinit var service: StoreServiceImpl

    @BeforeEach
    fun setUp() {
        service = StoreServiceImpl(synchronizationService, repository, cache)
    }

    @Test
    fun `returns cached page without synchronizing or querying repository`() {
        val query = TestDataFactory.query()
        val cached = TestDataFactory.page()
        `when`(cache.getPage(query)).thenReturn(cached)

        StepVerifier
            .create(service.findAll(query))
            .expectNext(cached)
            .verifyComplete()

        verifyNoInteractions(synchronizationService, repository)
        verify(cache).getPage(query)
    }

    @Test
    fun `synchronizes stale snapshot clears pages and caches repository page`() {
        val query = TestDataFactory.query()
        val expected = TestDataFactory.page()
        `when`(cache.getPage(query)).thenReturn(null)
        `when`(cache.hasFreshSnapshot()).thenReturn(false)
        `when`(synchronizationService.synchronize()).thenReturn(true)
        `when`(repository.findPage(query.page, query.size)).thenReturn(expected)

        StepVerifier
            .create(service.findAll(query))
            .expectNext(expected)
            .verifyComplete()

        verify(cache).clearPages()
        verify(cache).markSnapshotFresh()
        verify(cache).savePage(query, expected)
    }

    @Test
    fun `uses existing snapshot without synchronizing`() {
        val query = TestDataFactory.query()
        val expected = TestDataFactory.page()
        `when`(cache.getPage(query)).thenReturn(null)
        `when`(cache.hasFreshSnapshot()).thenReturn(true)
        `when`(repository.findPage(query.page, query.size)).thenReturn(expected)

        StepVerifier
            .create(service.findAll(query))
            .expectNext(expected)
            .verifyComplete()

        verifyNoInteractions(synchronizationService)
        verify(cache).savePage(query, expected)
    }

    @Test
    fun `continues using repository when synchronization fails`() {
        val query = TestDataFactory.query()
        val expected = TestDataFactory.page()
        `when`(cache.getPage(query)).thenReturn(null)
        `when`(cache.hasFreshSnapshot()).thenReturn(false)
        `when`(synchronizationService.synchronize()).thenReturn(false)
        `when`(repository.findPage(query.page, query.size)).thenReturn(expected)

        StepVerifier
            .create(service.findAll(query))
            .expectNext(expected)
            .verifyComplete()

        verify(repository).findPage(query.page, query.size)
        verify(cache).savePage(query, expected)
    }

    @Test
    fun `find by id refreshes stale snapshot and returns location`() {
        val expected = TestDataFactory.location()
        `when`(cache.hasFreshSnapshot()).thenReturn(false)
        `when`(synchronizationService.synchronize()).thenReturn(true)
        `when`(repository.findById(expected.id)).thenReturn(expected)

        StepVerifier
            .create(service.findById(expected.id))
            .expectNext(expected)
            .verifyComplete()

        verify(cache).clearPages()
        verify(cache).markSnapshotFresh()
    }

    @Test
    fun `find by id emits empty when location does not exist`() {
        `when`(cache.hasFreshSnapshot()).thenReturn(true)
        `when`(repository.findById(999L)).thenReturn(null)

        StepVerifier
            .create(service.findById(999L))
            .verifyComplete()
    }
}
