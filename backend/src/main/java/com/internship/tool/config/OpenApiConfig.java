package com.internship.tool.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI policyLifecycleManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Policy Lifecycle Manager API")
                        .description("REST API for managing insurance policies, authentication, and audit logging")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Java Developer 2")
                                .email("dev2@internship.tool")));
    }
}
