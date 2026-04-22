package com.stroheim.app.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Spring security JWT asymmetric encryption demo",
                        email = "example@example.com",
                        url = "example.com"
                ),
                description = "Spring security JWT asymmetric encryption demo API documentation",
                title = "OpenAPI specification - Spring security JWT asymmetric encryption demo",
                version = "v1.0.0",
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
                ,termsOfService = "Terms of service URL"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Production ENV",
                        url = "https://api.example.com"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bear",
        description = "JWT Bearer token authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
