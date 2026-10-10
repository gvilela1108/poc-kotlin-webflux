package com.sandboxclient.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("local", "dev")
class OpenApiConfig {
    @Bean
    fun sandboxclientOpenAPI(): OpenAPI =
        OpenAPI().info(
            Info()
                .title("SandboxClient Stores API")
                .version("v1")
                .description(
                    "API for retrieving store locations from the synchronized snapshot. " +
                        "Responses support JSON (application/json), XML (application/xml), " +
                        "and CSV (text/csv), selected through the Accept header.",
                ),
        )
}
