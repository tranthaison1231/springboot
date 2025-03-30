package com.shopdevjava.springboot.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for Swagger/OpenAPI documentation
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:User Management API}")
    private String applicationName;

    @Bean
    public OpenAPI userManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName)
                        .description("RESTful API for User Management using Spring Boot, following Clean Architecture principles")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ShopDevJava")
                                .url("https://shopdevjava.com")
                                .email("info@shopdevjava.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("/").description("Local server")
                ));
    }
} 