package com.sandboxclient.controller

import com.sandboxclient.config.log.LogMessagesEnum.STORE_FETCH_ALL_REQUEST
import com.sandboxclient.config.log.LogMessagesEnum.STORE_FETCH_BY_ID_NOT_FOUND
import com.sandboxclient.config.log.LogMessagesEnum.STORE_FETCH_BY_ID_REQUEST
import com.sandboxclient.config.log.LoggerHandler
import com.sandboxclient.config.util.LocationFormatterRegistry
import com.sandboxclient.controller.request.StorePageRequest
import com.sandboxclient.controller.response.ApiErrorResponse
import com.sandboxclient.controller.response.StoreLocationResponse
import com.sandboxclient.controller.response.StorePageResponse
import com.sandboxclient.service.StoreService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/stores")
class StoreController(
    private val storeService: StoreService,
    private val formatterRegistry: LocationFormatterRegistry,
) {
    @Operation(
        summary = "Get stores",
        description =
            "Returns a page of stores. Select the response format with the Accept header: " +
                "application/json, application/xml, or text/csv.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Stores returned successfully",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = StorePageResponse::class)),
                    Content(mediaType = "application/xml", schema = Schema(implementation = StorePageResponse::class)),
                    Content(mediaType = "text/csv", schema = Schema(type = "string")),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid pagination parameters",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(responseCode = "406", description = "Requested response format is not supported"),
            ApiResponse(
                responseCode = "503",
                description = "External service is temporarily unavailable",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Unexpected internal error",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("", "/")
    fun list(
        @Valid @ParameterObject @ModelAttribute request: StorePageRequest,
        @Parameter(hidden = true) serverRequest: ServerHttpRequest,
    ): Mono<ResponseEntity<Any>> {
        val formatter = formatterRegistry.resolve(serverRequest.headers.accept)
        val query = request.toDomain()

        LoggerHandler.info(log, STORE_FETCH_ALL_REQUEST, query.page, query.size)

        return storeService
            .findAll(query)
            .map { page ->
                val response = StorePageResponse.fromDomain(page)

                ResponseEntity
                    .ok()
                    .contentType(formatter.mediaType())
                    .body(formatter.format(response))
            }
    }

    @Operation(
        summary = "Get store by ID",
        description = "Returns one store by its SandboxClient location ID. Select JSON, XML, or CSV with the Accept header.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Store found",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = StoreLocationResponse::class)),
                    Content(mediaType = "application/xml", schema = Schema(implementation = StoreLocationResponse::class)),
                    Content(mediaType = "text/csv", schema = Schema(type = "string")),
                ],
            ),
            ApiResponse(responseCode = "404", description = "Store not found"),
            ApiResponse(responseCode = "406", description = "Requested response format is not supported"),
            ApiResponse(
                responseCode = "503",
                description = "External service is temporarily unavailable",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Unexpected internal error",
                content = [Content(schema = Schema(implementation = ApiErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
        @Parameter(hidden = true) serverRequest: ServerHttpRequest,
    ): Mono<ResponseEntity<Any>> {
        val formatter = formatterRegistry.resolve(serverRequest.headers.accept)

        LoggerHandler.info(log, STORE_FETCH_BY_ID_REQUEST, id)

        return storeService
            .findById(id)
            .map { location ->
                ResponseEntity
                    .ok()
                    .contentType(formatter.mediaType())
                    .body<Any>(formatter.format(location))
            }.switchIfEmpty(
                Mono.fromSupplier<ResponseEntity<Any>> {
                    LoggerHandler.info(log, STORE_FETCH_BY_ID_NOT_FOUND, id)
                    ResponseEntity.notFound().build<Any>()
                },
            )
    }

    private companion object {
        val log = LoggerFactory.getLogger(StoreController::class.java)
    }
}
