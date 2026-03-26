package com.efe.requestmanagementapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI requestManagementOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Request Management API")
                .description("API for creating, listing, updating and deleting requests")
                .version("v1.0")
                .contact(new Contact().name("Request Management API")));
    }
}