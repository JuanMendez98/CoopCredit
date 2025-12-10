package com.crudzaso.riskapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration for Risk Central Service
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoopCredit Risk Central API")
                        .version("1.0.0")
                        .description("Risk Assessment and Scoring System - Evaluates credit risk based on document, amount, and term")
                        .contact(new Contact()
                                .name("CoopCredit Support")
                                .email("support@coopcredit.com")
                                .url("https://coopcredit.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}