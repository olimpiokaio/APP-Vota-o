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
@ActiveProfiles("tes")
class TesProfileIntegrationTest {

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
    @DisplayName("Deve carregar o bean TesAmbienteService para o profile TES")
    void deveCarregarServicoTesParaProfileTes() {
        assertNotNull(ambienteService);
        assertInstanceOf(TesAmbienteService.class, ambienteService);
        assertEquals("TES", ambienteService.getCodigoAmbiente());
        assertFalse(ambienteService.isProducao());
        assertTrue(ambienteService.isDocumentacaoHabilitada());
    }

    @Test
    @DisplayName("Deve carregar a infraestrutura e configurações específicas de TES")
    void deveCarregarInfraestruturaTes() {
        assertNotNull(infraestruturaAmbiente);
        assertEquals("TES", infraestruturaAmbiente.ambiente());
        assertFalse(infraestruturaAmbiente.consoleHabilitado());
        assertEquals("TEST_STAGING", infraestruturaAmbiente.nivelSeguranca());
    }

    @Test
    @DisplayName("Deve validar as propriedades injetadas para o profile TES")
    void deveValidarPropriedadesTes() {
        assertEquals("https://tes-api.exemplo.com", baseUrl);
        assertFalse(h2ConsoleEnabled);
        assertTrue(swaggerUiEnabled);
    }

    @Test
    @DisplayName("NÃO deve carregar beans de DES ou PROD quando o profile ativo for TES")
    void naoDeveCarregarBeansDeOutrosPerfis() {
        assertFalse(applicationContext.containsBean("desProfileConfig"), "Bean desProfileConfig não deve existir no contexto de TES");
        assertFalse(applicationContext.containsBean("prodProfileConfig"), "Bean prodProfileConfig não deve existir no contexto de TES");
        assertFalse(applicationContext.containsBean("desAmbienteService"), "Bean desAmbienteService não deve existir no contexto de TES");
        assertFalse(applicationContext.containsBean("prodAmbienteService"), "Bean prodAmbienteService não deve existir no contexto de TES");
    }
}
