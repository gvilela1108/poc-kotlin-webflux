package com.sandboxclient.client.sandboxclient

import com.fasterxml.jackson.databind.ObjectMapper
import feign.codec.Decoder
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

class SandboxClientFeignConfig {
    @Bean
    fun sandboxclientFeignDecoder(objectMapper: ObjectMapper): Decoder {
        // Do not add Boot's default converters here: jackson-dataformat-xml is
        // on the classpath and could register an XML converter for JSON responses.
        val converters =
            HttpMessageConverters(
                false,
                listOf(MappingJackson2HttpMessageConverter(objectMapper)),
            )

        val messageConvertersFactory =
            ObjectFactory { converters }

        return SpringDecoder(messageConvertersFactory)
    }
}
