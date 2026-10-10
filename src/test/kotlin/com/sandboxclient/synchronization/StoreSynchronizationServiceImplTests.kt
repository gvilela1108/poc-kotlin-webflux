package com.sandboxclient.synchronization

import com.sandboxclient.config.exception.ExternalServiceException
import com.sandboxclient.provider.StoreProvider
import com.sandboxclient.repository.StoreRepository
import com.sandboxclient.service.StoreSynchronizationServiceImpl
import com.sandboxclient.support.TestDataFactory
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`

class StoreSynchronizationServiceImplTests {
    private val provider = mock(StoreProvider::class.java)
    private val repository = mock(StoreRepository::class.java)
    private lateinit var service: StoreSynchronizationServiceImpl

    @BeforeEach
    fun setUp() {
        service = StoreSynchronizationServiceImpl(provider, repository)
    }

    @Test
    fun `replaces repository snapshot when provider returns locations`() {
        val locations = listOf(TestDataFactory.location())
        `when`(provider.findAll()).thenReturn(locations)

        assertTrue(service.synchronize())

        verify(repository).replaceAll(locations)
    }

    @Test
    fun `returns false and preserves repository when provider returns no locations`() {
        `when`(provider.findAll()).thenReturn(emptyList())

        assertFalse(service.synchronize())

        verifyNoInteractions(repository)
    }

    @Test
    fun `rethrows external service error when local snapshot is empty`() {
        val exception = ExternalServiceException("provider down")
        `when`(provider.findAll()).thenThrow(exception)
        `when`(repository.count()).thenReturn(0)

        val thrown = assertThrows<ExternalServiceException> { service.synchronize() }

        assertTrue(thrown === exception)
    }

    @Test
    fun `keeps current snapshot when provider fails and repository has data`() {
        `when`(provider.findAll()).thenThrow(ExternalServiceException("provider down"))
        `when`(repository.count()).thenReturn(3)

        assertFalse(service.synchronize())

        verify(repository).count()
        verify(repository, org.mockito.Mockito.never()).replaceAll(org.mockito.ArgumentMatchers.anyList())
    }
}
