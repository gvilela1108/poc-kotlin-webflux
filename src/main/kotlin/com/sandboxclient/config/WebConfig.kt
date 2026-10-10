package com.sandboxclient.config

import com.sandboxclient.service.SandboxClientApiClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class WebConfig(
    @Value("\${sandboxclient.api.base-url}") private val baseUrl: String,
) {
    @Bean
    fun webClient(): WebClient =
        WebClient
            .builder()
            .baseUrl(baseUrl)
            .build()

    @Bean
    fun sandboxclientApiClient(webClient: WebClient): SandboxClientApiClient {
        val httpServiceProxyFactory =
            HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build()
        return httpServiceProxyFactory.createClient(SandboxClientApiClient::class.java)
    }
}
