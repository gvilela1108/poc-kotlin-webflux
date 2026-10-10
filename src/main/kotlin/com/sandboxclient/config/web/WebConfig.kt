package com.sandboxclient.config.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
class WebConfig {
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> =
        RedisTemplate<String, String>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = StringRedisSerializer()
            afterPropertiesSet()
        }

    /**
     * Keep JSON and XML mappers as distinct beans. XmlMapper extends ObjectMapper,
     * so without an explicit primary JSON mapper Spring may inject the XML mapper
     * wherever an ObjectMapper is requested.
     */
    @Bean
    @Primary
    fun objectMapper(): ObjectMapper = ObjectMapper().findAndRegisterModules()

    @Bean
    fun xmlMapper(): XmlMapper = XmlMapper.builder().findAndAddModules().build()

    /**
     * Spring Cloud OpenFeign expects Spring Boot's HttpMessageConverters bean.
     * WebFlux does not create this servlet-oriented bean automatically, so we
     * provide the converters explicitly while keeping the application reactive.
     */
    @Bean
    fun feignHttpMessageConverters(objectMapper: ObjectMapper): HttpMessageConverters =
        HttpMessageConverters(
            false,
            listOf(MappingJackson2HttpMessageConverter(objectMapper)),
        )
}
