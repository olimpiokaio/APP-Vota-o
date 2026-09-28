package com.example.demo.config.environment;

public record InfraestruturaAmbiente(
        String ambiente,
        String tipoBanco,
        String nivelSeguranca,
        boolean consoleHabilitado
) {}
