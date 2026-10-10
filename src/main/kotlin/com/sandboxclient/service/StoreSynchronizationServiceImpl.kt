package com.sandboxclient.service

import com.sandboxclient.config.exception.ExternalServiceException
import com.sandboxclient.config.log.LogMessagesEnum.STORE_SYNCHRONIZATION_EMPTY
import com.sandboxclient.config.log.LogMessagesEnum.STORE_SYNCHRONIZATION_FAILED
import com.sandboxclient.config.log.LoggerHandler
import com.sandboxclient.provider.StoreProvider
import com.sandboxclient.repository.StoreRepository
import com.sandboxclient.synchronization.StoreSynchronizationService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StoreSynchronizationServiceImpl(
    private val provider: StoreProvider,
    private val repository: StoreRepository,
) : StoreSynchronizationService {
    override fun synchronize(): Boolean =
        try {
            val locations = provider.findAll()

            if (locations.isEmpty()) {
                LoggerHandler.warn(
                    log,
                    STORE_SYNCHRONIZATION_EMPTY,
                )
                false
            } else {
                repository.replaceAll(locations)
                true
            }
        } catch (exception: ExternalServiceException) {
            if (repository.count() == 0) throw exception

            LoggerHandler.warn(
                log,
                STORE_SYNCHRONIZATION_FAILED,
                exception,
            )
            false
        }

    private companion object {
        val log =
            LoggerFactory.getLogger(
                StoreSynchronizationServiceImpl::class.java,
            )
    }
}
