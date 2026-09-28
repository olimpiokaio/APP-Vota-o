package com.example.demo;

import com.example.demo.config.environment.AmbienteService;
import com.example.demo.config.environment.DesAmbienteService;
import com.example.demo.config.environment.InfraestruturaAmbiente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private AmbienteService ambienteService;

	@Autowired
	private InfraestruturaAmbiente infraestruturaAmbiente;

	@Test
	@DisplayName("Deve carregar o contexto padrão com o perfil DES por default")
	void contextLoads() {
		assertNotNull(ambienteService);
		assertInstanceOf(DesAmbienteService.class, ambienteService);
		assertEquals("DES", ambienteService.getCodigoAmbiente());
		assertNotNull(infraestruturaAmbiente);
		assertEquals("DES", infraestruturaAmbiente.ambiente());
	}

}
