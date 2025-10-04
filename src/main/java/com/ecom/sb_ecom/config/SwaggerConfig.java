package com.ecom.sb_ecom.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Token");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot E-com")
                        .description("This is spring boot project for E-com Application")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0"))
                        .contact(new Contact().name("Suraj Wadikar").email("wsurajml007@gmail.com"))
                )
                .externalDocs(new ExternalDocumentation().description("Project Documentation").url("https://spring.io/"))
                .components(
                        new Components()
                                .addSecuritySchemes("Bearer Authentication", bearerScheme)
                ).addSecurityItem(securityRequirement);
    }
}
