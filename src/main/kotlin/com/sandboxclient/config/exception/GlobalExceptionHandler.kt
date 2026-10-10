
package com.sandboxclient.config.exception

import com.sandboxclient.config.log.LogMessagesEnum
import com.sandboxclient.config.log.LogMessagesEnum.ARGUMENTS_ERROR
import com.sandboxclient.config.log.LogMessagesEnum.UNEXPECTED_ERROR
import com.sandboxclient.config.log.LoggerHandler
import com.sandboxclient.controller.response.ApiErrorResponse
import feign.FeignException
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.resource.NoResourceFoundException
import org.springframework.web.server.NotAcceptableStatusException
import org.springframework.web.server.ServerWebInputException
import java.time.OffsetDateTime
import java.util.UUID

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<ApiErrorResponse> {
        LoggerHandler.warn(log, ARGUMENTS_ERROR, ex.message)

        return error(
            HttpStatus.BAD_REQUEST,
            "BAD_REQUEST",
            ex.message ?: "Invalid request",
        )
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleInvalidInput(ex: ServerWebInputException): ResponseEntity<ApiErrorResponse> {
        log.warn("Invalid request input: {}", ex.reason)

        return error(
            HttpStatus.BAD_REQUEST,
            "BAD_REQUEST",
            "Invalid request parameter",
        )
    }

    @ExceptionHandler(
        FeignException::class,
        CallNotPermittedException::class,
        ExternalServiceException::class,
    )
    fun handleExternalService(ex: Exception): ResponseEntity<ApiErrorResponse> {
        LoggerHandler.warn(
            log,
            LogMessagesEnum.EXTERNAL_SERVICE_UNAVAILABLE,
            ex,
        )

        return error(
            HttpStatus.SERVICE_UNAVAILABLE,
            "EXTERNAL_SERVICE_UNAVAILABLE",
            "External service is temporarily unavailable",
        )
    }

    @ExceptionHandler(NotAcceptableStatusException::class)
    fun handleNotAcceptable(): ResponseEntity<Void> = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build()

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleResourceNotFound(ex: NoResourceFoundException): ResponseEntity<ApiErrorResponse> =
        error(
            HttpStatus.NOT_FOUND,
            "NOT_FOUND",
            "Resource not found",
        )

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidationException(exception: WebExchangeBindException): ResponseEntity<ApiErrorResponse> {
        val message =
            exception.bindingResult.fieldErrors
                .joinToString("; ") { fieldError ->
                    "${fieldError.field}: ${fieldError.defaultMessage}"
                }

        return error(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            message.ifBlank { "Invalid request" },
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<ApiErrorResponse> {
        LoggerHandler.error(
            log,
            UNEXPECTED_ERROR,
            ex,
            "Unhandled exception",
        )

        return error(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "An unexpected error occurred",
        )
    }

    private fun error(
        status: HttpStatus,
        code: String,
        message: String,
    ): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(status).body(
            ApiErrorResponse(
                timestamp = OffsetDateTime.now(),
                status = status.value(),
                code = code,
                message = message,
                correlationId =
                    MDC.get("correlationId")
                        ?: UUID.randomUUID().toString(),
            ),
        )

    private companion object {
        val log =
            LoggerFactory.getLogger(
                GlobalExceptionHandler::class.java,
            )
    }
}
