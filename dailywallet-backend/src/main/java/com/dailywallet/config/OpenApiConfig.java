package com.dailywallet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI dailyWalletOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DailyWallet API")
                        .description("Smart cash flow management backend for daily earners in Kenya. " +
                                "Features multi-wallet allocation, M-Pesa integration, and automated interest calculations.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DailyWallet Team")
                                .email("support@dailywallet.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://dailywallet.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.dailywallet.com")
                                .description("Production Server")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from login/register endpoint")));
    }
}
