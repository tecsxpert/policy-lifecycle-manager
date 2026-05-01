package com.internship.tool.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI policyLifecycleManagerOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Policy Lifecycle Manager API")
                        .description("REST API for managing insurance policies, authentication, and audit logging. " +
                                "All protected endpoints require a JWT token in the Authorization header: Bearer <token>")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Java Developer 2")
                                .email("dev2@internship.tool")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
