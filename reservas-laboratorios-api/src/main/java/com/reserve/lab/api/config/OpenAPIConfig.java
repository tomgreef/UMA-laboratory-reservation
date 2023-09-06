package com.reserve.lab.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Value("${openapi.url}")
    private String url;

    @Bean
    public OpenAPI myOpenAPI() {
        Server server = new Server();
        server.setUrl(url);

        Contact contact = new Contact();
        contact.setEmail("gluque@uma.es");
        contact.setName("Gabriel Jesús Luque Polo");

        Info info = new Info()
                .title("Sistema de reservas de laboratorios para el departamento de LCC - API")
                .version("1.0")
                .contact(contact)
                .description("Esta API expone puntos de acceso para crear reservas de laboratorios.");

        return new OpenAPI().info(info).servers(List.of(server));
    }
}