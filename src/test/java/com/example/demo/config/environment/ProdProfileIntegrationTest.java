package com.example.demo.config.environment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("prod")
@TestPropertySource(properties = {
        "JPA_DDL_AUTO=update",
        "DB_URL=jdbc:h2:mem:votedb_prod_test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
class ProdProfileIntegrationTest {

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
    @DisplayName("Deve carregar o bean ProdAmbienteService para o profile PROD")
    void deveCarregarServicoProdParaProfileProd() {
        assertNotNull(ambienteService);
        assertInstanceOf(ProdAmbienteService.class, ambienteService);
        assertEquals("PROD", ambienteService.getCodigoAmbiente());
        assertTrue(ambienteService.isProducao());
        assertFalse(ambienteService.isDocumentacaoHabilitada());
    }

    @Test
    @DisplayName("Deve carregar a infraestrutura e configurações específicas de PROD")
    void deveCarregarInfraestruturaProd() {
        assertNotNull(infraestruturaAmbiente);
        assertEquals("PROD", infraestruturaAmbiente.ambiente());
        assertFalse(infraestruturaAmbiente.consoleHabilitado());
        assertEquals("PROD_STRICT", infraestruturaAmbiente.nivelSeguranca());
    }

    @Test
    @DisplayName("Deve validar as propriedades injetadas para o profile PROD")
    void deveValidarPropriedadesProd() {
        assertEquals("https://api.exemplo.com", baseUrl);
        assertFalse(h2ConsoleEnabled);
        assertFalse(swaggerUiEnabled);
    }

    @Test
    @DisplayName("NÃO deve carregar beans de DES ou TES quando o profile ativo for PROD")
    void naoDeveCarregarBeansDeOutrosPerfis() {
        assertFalse(applicationContext.containsBean("desProfileConfig"), "Bean desProfileConfig não deve existir no contexto de PROD");
        assertFalse(applicationContext.containsBean("tesProfileConfig"), "Bean tesProfileConfig não deve existir no contexto de PROD");
        assertFalse(applicationContext.containsBean("desAmbienteService"), "Bean desAmbienteService não deve existir no contexto de PROD");
        assertFalse(applicationContext.containsBean("tesAmbienteService"), "Bean tesAmbienteService não deve existir no contexto de PROD");
    }
}
