package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenApiConfigTest {

    @Test
    public void testOpenAPIConfiguration() {
        OpenApiConfig openApiConfig = new OpenApiConfig();

        OpenAPI openAPI = openApiConfig.openAPI();

        assertNotNull(openAPI);

        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("Bookstore API", info.getTitle());
        assertEquals("API for managing a bookstore", info.getDescription());
        assertEquals("v1.0", info.getVersion());

        assertFalse(openAPI.getSecurity().isEmpty());
        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertTrue(securityRequirement.containsKey("basicAuth"));

        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("basicAuth"));
        
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("basicAuth");
        assertNotNull(securityScheme);
        assertEquals("basicAuth", securityScheme.getName());
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("basic", securityScheme.getScheme());
    }
}