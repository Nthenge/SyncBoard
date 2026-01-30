package com.eclectics.collaboration.Tool.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Collaboration Tool API")
                        .version("1.0.0")
                        .description("API documentation for Collaboration Tool")
                        .contact(new Contact()
                                .name("Abraham Mutinda")
                                .email("abrahamnetsec@gmail.com"))
                        .license(new License()
                                .name("Apache 1.0")
                                .url("http://springdoc.org"))
                );
    }
}
