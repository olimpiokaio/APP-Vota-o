package com.example.demo.config;

import com.example.demo.config.environment.AmbienteService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final AmbienteService ambienteService;

    @Bean
    public OpenAPI customOpenAPI() {
        String prefixoAmbiente = ambienteService != null ? "[" + ambienteService.getCodigoAmbiente() + "] " : "";
        String detalheAmbiente = ambienteService != null ? " | Ambiente: " + ambienteService.getDescricao() : "";

        return new OpenAPI()
                .info(new Info()
                        .title(prefixoAmbiente + "API de Gestão de Votações em Assembleias Cooperativas")
                        .description("Backend RESTful para gestão de pautas, controle de sessões de votação, apuração de votos e suporte a Server-Driven UI (SDUI)." + detalheAmbiente)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipe de Engenharia Backend")
                                .email("contato@exemplo.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
