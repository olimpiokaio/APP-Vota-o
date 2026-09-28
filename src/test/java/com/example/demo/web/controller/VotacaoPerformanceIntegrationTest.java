package com.example.demo.web.controller;

import com.example.demo.domain.model.OpcaoVoto;
import com.example.demo.domain.repository.VotoRepository;
import com.example.demo.infrastructure.client.dto.CpfStatus;
import com.example.demo.service.CpfValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Slf4j
class VotacaoPerformanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VotoRepository votoRepository;

    @MockitoBean
    private CpfValidationService cpfValidationService;

    @BeforeEach
    void setUp() {
        when(cpfValidationService.validarCpfParaVotacao(anyString())).thenReturn(CpfStatus.ABLE_TO_VOTE);
    }

    /**
     * Gera CPFs válidos matematicamente com 11 dígitos determinísticos para os testes de carga.
     */
    private String gerarCpfValido(int indice) {
        return String.format("%011d", 10000000000L + (indice % 89999999999L));
    }

    @Test
    @DisplayName("Performance & Concorrência - 100 threads votando simultaneamente com CPFs únicos na pauta 2")
    void deveSuportarCargaConcorrenteDeVotosComCpfsDistintos() throws Exception {
        int totalRequisicoes = 100;
        int threadsConcorrentes = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threadsConcorrentes);
        CountDownLatch latchInicio = new CountDownLatch(1);
        CountDownLatch latchFim = new CountDownLatch(totalRequisicoes);

        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);
        List<Long> latenciasMs = Collections.synchronizedList(new ArrayList<>());

        long tempoInicioGeral = System.currentTimeMillis();

        for (int i = 0; i < totalRequisicoes; i++) {
            final int idAssociado = i + 1000;
            final String cpf = gerarCpfValido(idAssociado);
            final String opcao = (i % 2 == 0) ? "SIM" : "NAO";

            executor.submit(() -> {
                try {
                    latchInicio.await(); // Aguarda disparo simultâneo de todas as threads
                    long inicioReq = System.currentTimeMillis();

                    String payload = objectMapper.writeValueAsString(Map.of(
                            "cpf", cpf,
                            "voto", opcao
                    ));

                    MvcResult result = mockMvc.perform(post("/api/v1/pautas/2/votos")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(payload))
                            .andReturn();

                    long latencia = System.currentTimeMillis() - inicioReq;
                    latenciasMs.add(latencia);

                    int statusCode = result.getResponse().getStatus();
                    if (statusCode == 201) {
                        sucessos.incrementAndGet();
                    } else {
                        falhas.incrementAndGet();
                        log.error("Falha inesperada no voto: status={}, corpo={}", statusCode, result.getResponse().getContentAsString());
                    }
                } catch (Exception e) {
                    falhas.incrementAndGet();
                    log.error("Exceção ao disparar voto concorrente", e);
                } finally {
                    latchFim.countDown();
                }
            });
        }

        // Libera todas as threads ao mesmo tempo (efeito 'tiro de partida' / spike)
        latchInicio.countDown();
        boolean finalizouNoTempo = latchFim.await(30, TimeUnit.SECONDS);
        long tempoTotalExecucaoMs = System.currentTimeMillis() - tempoInicioGeral;

        executor.shutdown();

        // Cálculo de Métricas de Performance
        assertThat(finalizouNoTempo).as("O teste de carga deve finalizar dentro do timeout esperado").isTrue();
        assertThat(sucessos.get()).isEqualTo(totalRequisicoes);
        assertThat(falhas.get()).isZero();

        double latenciaMedia = latenciasMs.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long latenciaMax = latenciasMs.stream().mapToLong(Long::longValue).max().orElse(0);
        long latenciaMin = latenciasMs.stream().mapToLong(Long::longValue).min().orElse(0);
        double throughputRps = (totalRequisicoes / (double) tempoTotalExecucaoMs) * 1000.0;

        log.info("==========================================================================");
        log.info("RESULTADO DO TESTE DE PERFORMANCE - VOTOS CONCORRENTES");
        log.info("Total de requisições: {}", totalRequisicoes);
        log.info("Sucessos (201 Created): {}", sucessos.get());
        log.info("Falhas: {}", falhas.get());
        log.info("Tempo total de teste: {} ms", tempoTotalExecucaoMs);
        log.info("Throughput estimado: {} req/s", String.format("%.2f", throughputRps));
        log.info("Latência média: {} ms | Mínima: {} ms | Máxima: {} ms",
                String.format("%.2f", latenciaMedia), latenciaMin, latenciaMax);
        log.info("==========================================================================");

        // Verificação de Integridade e Contagem no Banco
        // Pauta 2 já possuía 1 voto no data-test.sql + 100 novos votos = 101
        long totalVotosNoBanco = votoRepository.countByPautaId(2L);
        assertThat(totalVotosNoBanco).isEqualTo(101);

        // Verificação da Apuração de Resultado sob Carga
        mockMvc.perform(get("/api/v1/pautas/2/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVotos").value(101));
    }

    @Test
    @DisplayName("Performance & Concorrência - Disparos concorrentes com o mesmo CPF devem computar exatamente 1 voto com sucesso e os demais 409 Conflict")
    void deveGarantirIdempotenciaEBloqueioConcorrenteDeVotoDuplicado() throws Exception {
        int threadsConcorrentes = 20;
        String mesmoCpf = "98765432100";

        ExecutorService executor = Executors.newFixedThreadPool(threadsConcorrentes);
        CountDownLatch latchInicio = new CountDownLatch(1);
        CountDownLatch latchFim = new CountDownLatch(threadsConcorrentes);

        AtomicInteger status201 = new AtomicInteger(0);
        AtomicInteger status409 = new AtomicInteger(0);

        for (int i = 0; i < threadsConcorrentes; i++) {
            final String opcao = (i % 2 == 0) ? "SIM" : "NAO";

            executor.submit(() -> {
                try {
                    latchInicio.await();

                    String payload = objectMapper.writeValueAsString(Map.of(
                            "cpf", mesmoCpf,
                            "voto", opcao
                    ));

                    MvcResult result = mockMvc.perform(post("/api/v1/pautas/2/votos")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(payload))
                            .andReturn();

                    int statusCode = result.getResponse().getStatus();
                    if (statusCode == 201) {
                        status201.incrementAndGet();
                    } else if (statusCode == 409) {
                        status409.incrementAndGet();
                    }
                } catch (Exception e) {
                    log.error("Erro na thread concorrente de voto duplicado", e);
                } finally {
                    latchFim.countDown();
                }
            });
        }

        latchInicio.countDown();
        boolean finalizou = latchFim.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(finalizou).isTrue();
        log.info("Concorrência de voto duplicado -> 201 Created: {}, 409 Conflict: {}", status201.get(), status409.get());

        // Exatamente 1 requisição deve ter sido gravada com sucesso e as restantes 19 bloqueadas
        assertThat(status201.get()).isEqualTo(1);
        assertThat(status409.get()).isEqualTo(threadsConcorrentes - 1);
    }
}
