package com.example.demo.config.environment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.core.env.StandardEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EnvironmentLoggerRunnerTest {

    @Test
    @DisplayName("Deve executar o EnvironmentLoggerRunner sem exceções em ambiente de teste")
    void deveExecutarLoggerSemExcecoes() {
        StandardEnvironment env = new StandardEnvironment();
        DesAmbienteService service = new DesAmbienteService();
        EnvironmentLoggerRunner runner = new EnvironmentLoggerRunner(env, service);

        assertDoesNotThrow(() -> runner.run(new DefaultApplicationArguments(new String[]{})));
    }
}
