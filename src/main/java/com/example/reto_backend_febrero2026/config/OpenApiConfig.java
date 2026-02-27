package com.example.reto_backend_febrero2026.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Servidor Localhost
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Entorno de Desarrollo (Local)");

        // Servidor Deploy
        Server deployServer = new Server();
        deployServer.setUrl("https://qa-reto-summer-pde-2026-invenzis-backend-133459896240.us-east1.run.app");
        deployServer.setDescription("Entorno de QA (Deploy)");

        return new OpenAPI().servers(List.of(localServer, deployServer));
    }
}