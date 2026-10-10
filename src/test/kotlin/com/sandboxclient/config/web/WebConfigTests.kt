package com.sandboxclient.config.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

class WebConfigTests {
    private val config = WebConfig()

    @Test
    fun `redisTemplate configures connection and string serializers`() {
        val connectionFactory =
            org.mockito.Mockito.mock(RedisConnectionFactory::class.java)

        val template = config.redisTemplate(connectionFactory)

        assertNotNull(template)
        assertSame(connectionFactory, template.connectionFactory)

        assertTrue(template.keySerializer is StringRedisSerializer)
        assertTrue(template.valueSerializer is StringRedisSerializer)
        assertTrue(template.hashKeySerializer is StringRedisSerializer)
        assertTrue(template.hashValueSerializer is StringRedisSerializer)
    }

    @Test
    fun `objectMapper creates primary JSON mapper with discovered modules`() {
        val mapper = config.objectMapper()

        assertNotNull(mapper)
        assertTrue(mapper is ObjectMapper)
        assertFalse(mapper is XmlMapper)

        // Confirma que o mapper consegue serializar e desserializar JSON.
        val json = mapper.writeValueAsString(mapOf("name" to "Store"))
        val result = mapper.readTree(json)

        assertEquals("Store", result["name"].asText())
    }

    @Test
    fun `xmlMapper creates distinct XML mapper`() {
        val mapper = config.xmlMapper()

        assertNotNull(mapper)
        assertTrue(mapper is XmlMapper)
        assertNotSame(config.objectMapper(), mapper)

        // Confirma que o mapper consegue processar XML.
        val xml = mapper.writeValueAsString(mapOf("name" to "Store"))
        assertTrue(xml.contains("Store"))
    }

    @Test
    fun `feignHttpMessageConverters registers JSON converter using supplied mapper`() {
        val objectMapper = config.objectMapper()

        val converters = config.feignHttpMessageConverters(objectMapper)

        assertNotNull(converters)

        val jacksonConverter =
            converters.converters
                .filterIsInstance<MappingJackson2HttpMessageConverter>()
                .firstOrNull()

        assertNotNull(jacksonConverter)
        assertSame(objectMapper, jacksonConverter!!.objectMapper)
    }
}
