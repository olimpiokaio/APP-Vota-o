package com.example.demo.config.environment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("des")
class DesProfileIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AmbienteService ambienteService;

    @Autowired
    private InfraestruturaAmbiente infraestruturaAmbiente;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.h2.console.enabled}")
    private boolean h2ConsoleEnabled;

    @Value("${springdoc.swagger-ui.enabled}")
    private boolean swaggerUiEnabled;

    @Test
    @DisplayName("Deve carregar o bean DesAmbienteService para o profile DES")
    void deveCarregarServicoDesParaProfileDes() {
        assertNotNull(ambienteService);
        assertInstanceOf(DesAmbienteService.class, ambienteService);
        assertEquals("DES", ambienteService.getCodigoAmbiente());
        assertFalse(ambienteService.isProducao());
        assertTrue(ambienteService.isDocumentacaoHabilitada());
    }

    @Test
    @DisplayName("Deve carregar a infraestrutura e configurações específicas de DES")
    void deveCarregarInfraestruturaDes() {
        assertNotNull(infraestruturaAmbiente);
        assertEquals("DES", infraestruturaAmbiente.ambiente());
        assertTrue(infraestruturaAmbiente.consoleHabilitado());
        assertEquals("DEV_PERMISSIVE", infraestruturaAmbiente.nivelSeguranca());
    }

    @Test
    @DisplayName("Deve validar as propriedades injetadas para o profile DES")
    void deveValidarPropriedadesDes() {
        assertEquals("http://localhost:8080", baseUrl);
        assertTrue(h2ConsoleEnabled);
        assertTrue(swaggerUiEnabled);
    }

    @Test
    @DisplayName("NÃO deve carregar beans de TES ou PROD quando o profile ativo for DES")
    void naoDeveCarregarBeansDeOutrosPerfis() {
        assertFalse(applicationContext.containsBean("tesProfileConfig"), "Bean tesProfileConfig não deve existir no contexto de DES");
        assertFalse(applicationContext.containsBean("prodProfileConfig"), "Bean prodProfileConfig não deve existir no contexto de DES");
        assertFalse(applicationContext.containsBean("tesAmbienteService"), "Bean tesAmbienteService não deve existir no contexto de DES");
        assertFalse(applicationContext.containsBean("prodAmbienteService"), "Bean prodAmbienteService não deve existir no contexto de DES");
    }
}
