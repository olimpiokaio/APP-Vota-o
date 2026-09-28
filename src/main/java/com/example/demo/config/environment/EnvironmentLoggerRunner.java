package com.example.demo.config.environment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class EnvironmentLoggerRunner implements ApplicationRunner {

    private final Environment environment;
    private final AmbienteService ambienteService;

    @Override
    public void run(ApplicationArguments args) {
        String[] activeProfiles = environment.getActiveProfiles();
        String activeProfileStr = (activeProfiles != null && activeProfiles.length > 0)
                ? String.join(", ", activeProfiles)
                : "default (" + ambienteService.getCodigoAmbiente().toLowerCase() + ")";

        String datasourceUrl = environment.getProperty("spring.datasource.url", "N/A");
        String username = environment.getProperty("spring.datasource.username", "N/A");
        String serverPort = environment.getProperty("server.port", "8080");
        String baseUrl = environment.getProperty("app.base-url", "N/A");

        log.info("==================================================================================");
        log.info(" [INICIALIZAÇÃO] Perfil Spring ativo: {}", activeProfileStr);
        log.info(" - Código do Ambiente: {}", ambienteService.getCodigoAmbiente());
        log.info(" - Descrição: {}", ambienteService.getDescricao());
        log.info(" - Porta HTTP: {}", serverPort);
        log.info(" - Base URL: {}", baseUrl);
        log.info(" - Conexão Banco: URL={}, Usuário={}, Senha=[PROTEGIDA/OCULTA]", datasourceUrl, username);
        log.info(" - Documentação OpenAPI/Swagger: {}", ambienteService.isDocumentacaoHabilitada() ? "HABILITADA" : "DESABILITADA");
        log.info("==================================================================================");
    }
}
