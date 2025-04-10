package com.backend.vet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {
    @Value("${api.info.title}")
    private String apiTitle;
    
    @Value("${api.info.description}")
    private String apiDescription;
    
    @Value("${api.info.version}")
    private String apiVersion;
    
    @Value("${api.info.terms-of-service}")
    private String apiTermsOfService;
    
    @Value("${api.info.contact.name}")
    private String apiContactName;
    
    @Value("${api.info.contact.url}")
    private String apiContactUrl;
    
    @Value("${api.info.contact.email}")
    private String apiContactEmail;
    
    @Value("${api.info.license.name}")
    private String apiLicenseName;
    
    @Value("${api.info.license.url}")
    private String apiLicenseUrl;
    
    private static final String SCHEME_NAME = "bearerAuth";
    private static final String SCHEME = "bearer";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME, 
                            new SecurityScheme()
                                .name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(SCHEME)
                                .bearerFormat("JWT")
                                .description("Introduce tu token JWT con el prefijo Bearer. Ejemplo: 'Bearer abcdef12345'")))
                .info(new Info()
                        .title(apiTitle)
                        .description(apiDescription)
                        .version(apiVersion)
                        .termsOfService(apiTermsOfService)
                        .contact(new Contact()
                                .name(apiContactName)
                                .url(apiContactUrl)
                                .email(apiContactEmail))
                        .license(new License()
                                .name(apiLicenseName)
                                .url(apiLicenseUrl)))
                .servers(Arrays.asList(
                        new Server().url("/").description("Servidor por defecto")
                ));
    }
}
