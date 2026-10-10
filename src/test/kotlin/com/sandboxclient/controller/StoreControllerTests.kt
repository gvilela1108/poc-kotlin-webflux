package com.sandboxclient.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.sandboxclient.config.exception.GlobalExceptionHandler
import com.sandboxclient.config.util.CsvLocationFormatter
import com.sandboxclient.config.util.JsonLocationFormatter
import com.sandboxclient.config.util.LocationFormatterRegistry
import com.sandboxclient.config.util.StoreMediaTypes
import com.sandboxclient.config.util.XmlLocationFormatter
import com.sandboxclient.controller.request.StorePageRequest
import com.sandboxclient.domain.StorePage
import com.sandboxclient.domain.StorePageQuery
import com.sandboxclient.service.StoreService
import com.sandboxclient.support.TestDataFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.server.NotAcceptableStatusException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class StoreControllerTests {
    private val service = mock(StoreService::class.java)
    private val request = mock(ServerHttpRequest::class.java)

    private val registry =
        LocationFormatterRegistry(
            listOf(
                JsonLocationFormatter(ObjectMapper().findAndRegisterModules()),
                XmlLocationFormatter(XmlMapper()),
                CsvLocationFormatter(ObjectMapper().findAndRegisterModules()),
            ),
        )

    private lateinit var controller: StoreController
    private lateinit var webClient: WebTestClient

    private val validator =
        LocalValidatorFactoryBean().apply {
            afterPropertiesSet()
        }

    @BeforeEach
    fun setUp() {
        controller = StoreController(service, registry)
        accept(MediaType.APPLICATION_JSON)

        webClient =
            WebTestClient
                .bindToController(controller)
                .controllerAdvice(GlobalExceptionHandler())
                .validator(validator)
                .build()
    }

    @Test
    fun `list returns JSON body and content type`() {
        val location = TestDataFactory.location(name = "Central Store")

        `when`(service.findAll(StorePageQuery(0, 10)))
            .thenReturn(Mono.just(StorePage(1, listOf(location))))

        StepVerifier
            .create(controller.list(StorePageRequest(0, 10), request))
            .assertNext { response ->
                assertEquals(200, response.statusCode.value())
                assertEquals(StoreMediaTypes.JSON, response.headers.contentType)

                val body = response.body.toString()

                assertTrue(body.contains("\"count\":1"))
                assertTrue(body.contains("Central Store"))
                assertTrue(body.contains("\"locations\""))
            }.verifyComplete()
    }

    @Test
    fun `list returns XML body and content type`() {
        accept(StoreMediaTypes.XML)

        val location = TestDataFactory.location(name = "XML Store")

        `when`(service.findAll(StorePageQuery(0, 10)))
            .thenReturn(Mono.just(StorePage(1, listOf(location))))

        StepVerifier
            .create(controller.list(StorePageRequest(0, 10), request))
            .assertNext { response ->
                assertEquals(200, response.statusCode.value())
                assertEquals(StoreMediaTypes.XML, response.headers.contentType)

                val body = response.body.toString()

                assertTrue(body.contains("XML Store"))
                assertTrue(body.contains("LocationList"))
            }.verifyComplete()
    }

    @Test
    fun `list XML response contains expected count and locations`() {
        accept(StoreMediaTypes.XML)

        val locations =
            listOf(
                TestDataFactory.location(id = 51, name = "First XML Store"),
                TestDataFactory.location(id = 52, name = "Second XML Store"),
            )

        `when`(service.findAll(StorePageQuery(0, 10)))
            .thenReturn(Mono.just(StorePage(2, locations)))

        StepVerifier
            .create(controller.list(StorePageRequest(0, 10), request))
            .assertNext { response ->
                assertEquals(200, response.statusCode.value())
                assertEquals(StoreMediaTypes.XML, response.headers.contentType)

                val xml = response.body.toString()

                assertTrue(xml.contains("<count>2</count>"))
                assertTrue(xml.contains("First XML Store"))
                assertTrue(xml.contains("Second XML Store"))
                assertTrue(xml.contains("<locations>"))
            }.verifyComplete()
    }

    @Test
    fun `list returns CSV body and content type`() {
        accept(StoreMediaTypes.CSV)

        val location = TestDataFactory.location(name = "CSV Store")

        `when`(service.findAll(StorePageQuery(0, 10)))
            .thenReturn(Mono.just(StorePage(1, listOf(location))))

        StepVerifier
            .create(controller.list(StorePageRequest(0, 10), request))
            .assertNext { response ->
                assertEquals(200, response.statusCode.value())
                assertEquals(StoreMediaTypes.CSV, response.headers.contentType)

                val body = response.body.toString()

                assertTrue(
                    body.startsWith(
                        "name,city,zip,streetAndNo,openingHours,keywords,lat,lng",
                    ),
                )
                assertTrue(body.contains("CSV Store"))
            }.verifyComplete()
    }

    @Test
    fun `list returns valid empty page when service has no locations`() {
        `when`(service.findAll(StorePageQuery(0, 10)))
            .thenReturn(Mono.just(StorePage(0, emptyList())))

        StepVerifier
            .create(controller.list(StorePageRequest(0, 10), request))
            .assertNext { response ->
                assertEquals(200, response.statusCode.value())
                assertEquals(StoreMediaTypes.JSON, response.headers.contentType)

                val body = response.body.toString()

                assertTrue(body.contains("\"count\":0"))
                assertTrue(body.contains("\"locations\":[]"))
            }.verifyComplete()
    }

    @Test
    fun `get returns JSON body and content type when found`() {
        val location =
            TestDataFactory.location(
                id = 42,
                name = "Found Store",
            )

        `when`(service.findById(42))
            .thenReturn(Mono.just(location))

        StepVerifier
            .create(controller.get(42, request))
            .assertNext { response ->
                assertEquals(200, response.statusCode.value())
                assertEquals(StoreMediaTypes.JSON, response.headers.contentType)

                val body = response.body.toString()

                assertTrue(body.contains("Found Store"))
                assertTrue(body.contains("\"id\":42"))
            }.verifyComplete()
    }

    @Test
    fun `get returns XML body and content type when found`() {
        accept(StoreMediaTypes.XML)

        val location =
            TestDataFactory.location(
                id = 43,
                name = "XML Store",
            )

        `when`(service.findById(43))
            .thenReturn(Mono.just(location))

        StepVerifier
            .create(controller.get(43, request))
            .assertNext { response ->
                assertEquals(200, response.statusCode.value())
                assertEquals(StoreMediaTypes.XML, response.headers.contentType)
                assertTrue(response.body.toString().contains("XML Store"))
            }.verifyComplete()
    }

    @Test
    fun `get returns CSV body and content type when found`() {
        accept(StoreMediaTypes.CSV)

        val location =
            TestDataFactory.location(
                id = 44,
                name = "CSV Store",
            )

        `when`(service.findById(44))
            .thenReturn(Mono.just(location))

        StepVerifier
            .create(controller.get(44, request))
            .assertNext { response ->
                assertEquals(200, response.statusCode.value())
                assertEquals(StoreMediaTypes.CSV, response.headers.contentType)

                val body = response.body.toString()

                assertTrue(body.contains("CSV Store"))
                assertTrue(body.contains("name,city,zip"))
            }.verifyComplete()
    }

    @Test
    fun `get returns not found when location is missing`() {
        `when`(service.findById(404L))
            .thenReturn(Mono.empty())

        StepVerifier
            .create(controller.get(404L, request))
            .assertNext { response ->
                assertEquals(404, response.statusCode.value())
                assertTrue(response.body == null)
            }.verifyComplete()
    }

    @Test
    fun `unsupported Accept format throws not acceptable`() {
        accept(MediaType.TEXT_PLAIN)

        assertThrows(NotAcceptableStatusException::class.java) {
            controller.get(1L, request)
        }
    }

    @Test
    fun `list rejects negative page through request validation`() {
        webClient
            .get()
            .uri("/api/stores?page=-1&size=10")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `list rejects zero size through request validation`() {
        webClient
            .get()
            .uri("/api/stores?page=0&size=0")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `list rejects size greater than maximum through request validation`() {
        webClient
            .get()
            .uri("/api/stores?page=0&size=1001")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `list accepts valid pagination through request validation`() {
        `when`(service.findAll(StorePageQuery(0, 1)))
            .thenReturn(Mono.just(StorePage(0, emptyList())))

        webClient
            .get()
            .uri("/api/stores?page=0&size=1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(StoreMediaTypes.JSON)
            .expectBody()
            .jsonPath("$.count")
            .isEqualTo(0)
            .jsonPath("$.locations")
            .isEmpty
    }

    private fun accept(mediaType: MediaType) {
        val headers =
            HttpHeaders().apply {
                accept = listOf(mediaType)
            }

        `when`(request.headers).thenReturn(headers)
    }
}
