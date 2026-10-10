package com.sandboxclient.config.log

import com.sandboxclient.config.util.JsonUtils
import org.slf4j.Logger
import org.slf4j.MDC

object LoggerHandler {
    private const val MDC_LOG_CODE = "logCode"
    private const val MDC_LOG_MESSAGE = "logMessage"
    private const val MDC_PAYLOAD = "payload"

    fun info(
        logger: Logger,
        logEnum: LogMessagesEnum,
        vararg params: Any?,
    ) {
        log(logger, LogLevel.INFO, logEnum, null, params)
    }

    fun warn(
        logger: Logger,
        logEnum: LogMessagesEnum,
        vararg params: Any?,
    ) {
        log(logger, LogLevel.WARN, logEnum, null, params)
    }

    fun error(
        logger: Logger,
        logEnum: LogMessagesEnum,
        throwable: Throwable,
        vararg params: Any?,
    ) {
        log(logger, LogLevel.ERROR, logEnum, throwable, params)
    }

    private fun log(
        logger: Logger,
        level: LogLevel,
        logEnum: LogMessagesEnum,
        throwable: Throwable?,
        params: Array<out Any?>,
    ) {
        try {
            MDC.put(MDC_LOG_CODE, logEnum.code)
            MDC.put(MDC_LOG_MESSAGE, logEnum.message)

            if (params.isNotEmpty()) {
                val payload = if (params.size == 1) params[0] else params
                MDC.put(MDC_PAYLOAD, JsonUtils.toJson(payload))
            }

            when (level) {
                LogLevel.INFO -> logger.info(logEnum.message)
                LogLevel.WARN -> logger.warn(logEnum.message)
                LogLevel.ERROR -> logger.error(logEnum.message, throwable)
            }
        } finally {
            clearLocalMdc()
        }
    }

    /**
     * Removes only the MDC keys managed by this handler.
     */
    private fun clearLocalMdc() {
        MDC.remove(MDC_LOG_CODE)
        MDC.remove(MDC_LOG_MESSAGE)
        MDC.remove(MDC_PAYLOAD)
    }

    private enum class LogLevel {
        INFO,
        WARN,
        ERROR,
    }
}
