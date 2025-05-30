package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenApiConfigTest {

    @Test
    public void testOpenAPIConfiguration() { // sprawdzenie czy konfiguracja OpenAPI jest poprawna
        OpenApiConfig openApiConfig = new OpenApiConfig();

        OpenAPI openAPI = openApiConfig.openAPI();

        assertNotNull(openAPI);

        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("Bookstore API", info.getTitle());
        assertEquals("API for managing a bookstore", info.getDescription());
        assertEquals("v1.0", info.getVersion());

        // Security configuration is now done via annotations
        // No need to check for security in the OpenAPI object
    }
}
