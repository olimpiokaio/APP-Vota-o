package com.example.demo.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SduiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // =========================================================================
    // 1. Endpoint: GET /api/v1/sdui/telas/pauta-form
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sdui/telas/pauta-form - Deve retornar contrato SDUI FORMULARIO para cadastro de pauta (200 OK)")
    void deveRetornarTelaCadastroPauta() throws Exception {
        mockMvc.perform(get("/api/v1/sdui/telas/pauta-form"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo", is("FORMULARIO")))
                .andExpect(jsonPath("$.titulo", is("Cadastrar Nova Pauta")))
                .andExpect(jsonPath("$.mensagem", containsString("Preencha os dados abaixo")))
                .andExpect(jsonPath("$.itens", hasSize(2)))
                .andExpect(jsonPath("$.itens[0].nome", is("titulo")))
                .andExpect(jsonPath("$.itens[0].tipo", is("TEXTO")))
                .andExpect(jsonPath("$.itens[0].obrigatorio", is(true)))
                .andExpect(jsonPath("$.itens[1].nome", is("descricao")))
                .andExpect(jsonPath("$.itens[1].tipo", is("TEXTO")))
                .andExpect(jsonPath("$.itens[1].obrigatorio", is(false)))
                .andExpect(jsonPath("$.botoes", hasSize(1)))
                .andExpect(jsonPath("$.botoes[0].texto", is("Salvar Pauta")))
                .andExpect(jsonPath("$.botoes[0].metodo", is("POST")))
                .andExpect(jsonPath("$.botoes[0].url", containsString("/api/v1/pautas")));
    }

    // =========================================================================
    // 2. Endpoint: GET /api/v1/sdui/telas/pautas
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sdui/telas/pautas - Deve retornar contrato SDUI SELECAO com pautas fake do H2 (200 OK)")
    void deveRetornarTelaSelecaoPautas() throws Exception {
        mockMvc.perform(get("/api/v1/sdui/telas/pautas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo", is("SELECAO")))
                .andExpect(jsonPath("$.titulo", is("Pautas Disponíveis")))
                .andExpect(jsonPath("$.opcoes", hasSize(6)))
                .andExpect(jsonPath("$.opcoes[0].id", is("6")))
                .andExpect(jsonPath("$.opcoes[0].titulo", is("Pauta Fake Encerrada Sem Votos")))
                .andExpect(jsonPath("$.opcoes[0].descricao", containsString("Sessão encerrada")))
                .andExpect(jsonPath("$.opcoes[0].acao.metodo", is("GET")))
                .andExpect(jsonPath("$.opcoes[0].acao.url", containsString("/api/v1/pautas/6/resultado")));
    }

    // =========================================================================
    // 3. Endpoint: GET /api/v1/sdui/telas/pautas/{id}/sessao-form
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sdui/telas/pautas/{id}/sessao-form - Deve retornar contrato SDUI FORMULARIO de abertura de sessão (200 OK)")
    void deveRetornarTelaAberturaSessaoParaPautaExistente() throws Exception {
        mockMvc.perform(get("/api/v1/sdui/telas/pautas/1/sessao-form"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo", is("FORMULARIO")))
                .andExpect(jsonPath("$.titulo", is("Abrir Sessão de Votação")))
                .andExpect(jsonPath("$.mensagem", is("Pauta: Pauta Fake Sem Sessao")))
                .andExpect(jsonPath("$.itens", hasSize(1)))
                .andExpect(jsonPath("$.itens[0].nome", is("duracaoMinutos")))
                .andExpect(jsonPath("$.itens[0].tipo", is("NUMERICO")))
                .andExpect(jsonPath("$.itens[0].valorPadrao", is(1)))
                .andExpect(jsonPath("$.botoes", hasSize(1)))
                .andExpect(jsonPath("$.botoes[0].texto", is("Iniciar Votação")))
                .andExpect(jsonPath("$.botoes[0].metodo", is("POST")))
                .andExpect(jsonPath("$.botoes[0].url", containsString("/api/v1/pautas/1/sessao")));
    }

    @Test
    @DisplayName("GET /api/v1/sdui/telas/pautas/{id}/sessao-form - Deve retornar 404 Not Found para pauta inexistente")
    void deveRetornar404AoBuscarTelaAberturaSessaoParaPautaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/sdui/telas/pautas/99999/sessao-form"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Recurso não encontrado")));
    }

    // =========================================================================
    // 4. Endpoint: GET /api/v1/sdui/telas/pautas/{id}/votar
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/sdui/telas/pautas/{id}/votar - Deve retornar contrato SDUI FORMULARIO de votação (200 OK)")
    void deveRetornarTelaVotacaoParaPautaExistente() throws Exception {
        mockMvc.perform(get("/api/v1/sdui/telas/pautas/2/votar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo", is("FORMULARIO")))
                .andExpect(jsonPath("$.titulo", is("Votar na Pauta")))
                .andExpect(jsonPath("$.mensagem", is("Pauta: Pauta Fake Com Sessao Aberta")))
                .andExpect(jsonPath("$.itens", hasSize(2)))
                .andExpect(jsonPath("$.itens[0].nome", is("cpf")))
                .andExpect(jsonPath("$.itens[0].tipo", is("TEXTO")))
                .andExpect(jsonPath("$.itens[0].obrigatorio", is(true)))
                .andExpect(jsonPath("$.itens[1].nome", is("voto")))
                .andExpect(jsonPath("$.itens[1].tipo", is("SELECAO")))
                .andExpect(jsonPath("$.itens[1].opcoes", contains("SIM", "NAO")))
                .andExpect(jsonPath("$.itens[1].obrigatorio", is(true)))
                .andExpect(jsonPath("$.botoes", hasSize(1)))
                .andExpect(jsonPath("$.botoes[0].texto", is("Confirmar Voto")))
                .andExpect(jsonPath("$.botoes[0].metodo", is("POST")))
                .andExpect(jsonPath("$.botoes[0].url", containsString("/api/v1/pautas/2/votos")));
    }

    @Test
    @DisplayName("GET /api/v1/sdui/telas/pautas/{id}/votar - Deve retornar 404 Not Found para pauta inexistente")
    void deveRetornar404AoBuscarTelaVotacaoParaPautaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/sdui/telas/pautas/99999/votar"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Recurso não encontrado")));
    }
}
