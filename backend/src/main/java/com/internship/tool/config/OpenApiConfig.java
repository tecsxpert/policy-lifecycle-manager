package com.internship.tool.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration
 * Provides OpenAPI bean configuration and JWT bearer token security setup for
 * Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure OpenAPI bean with metadata, description, and JWT security scheme
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Policy Lifecycle Manager API")
                        .version("1.0.0")
                        .description("REST API for Policy Lifecycle Management System with JWT Authentication")
                        .contact(new Contact()
                                .name("Policy Lifecycle Manager")
                                .url("https://github.com/policy-lifecycle-manager"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer JWT"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer JWT", new SecurityScheme()
                                .type(Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token (obtained from /auth/login endpoint)")));
    }
}
