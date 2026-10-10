package com.sandboxclient.config.util

import com.sandboxclient.config.log.LogMessagesEnum.APP_SHUTDOWN
import com.sandboxclient.config.log.LogMessagesEnum.APP_STARTED
import com.sandboxclient.config.log.LoggerHandler
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ApplicationListener {
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        LoggerHandler.info(log, APP_STARTED)
    }

    @PreDestroy
    fun onShutdown() {
        LoggerHandler.info(log, APP_SHUTDOWN)
    }

    private companion object {
        val log = LoggerFactory.getLogger(ApplicationListener::class.java)
    }
}
