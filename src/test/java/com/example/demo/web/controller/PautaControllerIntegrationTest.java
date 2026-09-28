package com.example.demo.web.controller;

import com.example.demo.exception.AssociadoInaptoException;
import com.example.demo.exception.CpfInvalidoException;
import com.example.demo.infrastructure.client.dto.CpfStatus;
import com.example.demo.service.CpfValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PautaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CpfValidationService cpfValidationService;

    @BeforeEach
    void setUp() {
        when(cpfValidationService.validarCpfParaVotacao(anyString())).thenReturn(CpfStatus.ABLE_TO_VOTE);
    }

    // =========================================================================
    // 1. Endpoints: POST /api/v1/pautas (Cadastro de Pautas)
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/pautas - Deve cadastrar uma nova pauta com sucesso (201 Created)")
    void deveCriarPautaComSucesso() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "titulo", "Nova Pauta para Teste de Integracao",
                "descricao", "Descricao detalhada da pauta de teste."
        ));

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo", is("Nova Pauta para Teste de Integracao")))
                .andExpect(jsonPath("$.descricao", is("Descricao detalhada da pauta de teste.")))
                .andExpect(jsonPath("$.statusSessao", is("NAO_INICIADA")))
                .andExpect(jsonPath("$.dataCriacao").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/pautas - Deve cadastrar pauta sem descricao com sucesso (201 Created)")
    void deveCriarPautaSemDescricaoComSucesso() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "titulo", "Pauta Apenas Com Titulo"
        ));

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo", is("Pauta Apenas Com Titulo")))
                .andExpect(jsonPath("$.descricao").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/v1/pautas - Deve retornar 400 Bad Request quando titulo for vazio")
    void deveRetornar400QuandoCriarPautaComTituloVazio() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "titulo", "   ",
                "descricao", "Descricao qualquer"
        ));

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Dados de requisição inválidos")))
                .andExpect(jsonPath("$.erros.titulo").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/pautas - Deve retornar 400 Bad Request quando titulo for ausente")
    void deveRetornar400QuandoCriarPautaComTituloAusente() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "descricao", "Sem titulo"
        ));

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Dados de requisição inválidos")))
                .andExpect(jsonPath("$.erros.titulo").isNotEmpty());
    }

    // =========================================================================
    // 2. Endpoints: GET /api/v1/pautas (Listagem de Pautas)
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/pautas - Deve listar todas as pautas populadas pelo script fake (200 OK)")
    void deveListarTodasAsPautasPreviamenteCadastradasPeloScript() throws Exception {
        mockMvc.perform(get("/api/v1/pautas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].id", is(6)))
                .andExpect(jsonPath("$[0].titulo", is("Pauta Fake Encerrada Sem Votos")))
                .andExpect(jsonPath("$[0].statusSessao", is("ENCERRADA")))
                .andExpect(jsonPath("$[4].id", is(2)))
                .andExpect(jsonPath("$[4].statusSessao", is("ABERTA")))
                .andExpect(jsonPath("$[5].id", is(1)))
                .andExpect(jsonPath("$[5].statusSessao", is("NAO_INICIADA")));
    }

    // =========================================================================
    // 3. Endpoints: GET /api/v1/pautas/{id} (Consulta de Pauta por ID)
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/pautas/{id} - Deve buscar pauta existente sem sessao por ID (200 OK)")
    void deveBuscarPautaPorIdComSucesso() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.titulo", is("Pauta Fake Sem Sessao")))
                .andExpect(jsonPath("$.statusSessao", is("NAO_INICIADA")));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id} - Deve buscar pauta com sessao aberta (200 OK)")
    void deveBuscarPautaComSessaoAbertaPorId() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.titulo", is("Pauta Fake Com Sessao Aberta")))
                .andExpect(jsonPath("$.statusSessao", is("ABERTA")));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id} - Deve retornar 404 Not Found ao buscar pauta inexistente")
    void deveRetornar404AoBuscarPautaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Recurso não encontrado")));
    }

    // =========================================================================
    // 4. Endpoints: POST /api/v1/pautas/{id}/sessao (Abertura de Sessão)
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/sessao - Deve abrir sessao informando duracao em minutos (201 Created)")
    void deveAbrirSessaoComDuracaoEmMinutos() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "duracaoMinutos", 10
        ));

        mockMvc.perform(post("/api/v1/pautas/1/sessao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.pautaId", is(1)))
                .andExpect(jsonPath("$.aberta", is(true)))
                .andExpect(jsonPath("$.status", is("ABERTA")))
                .andExpect(jsonPath("$.dataHoraInicio").isNotEmpty())
                .andExpect(jsonPath("$.dataHoraFim").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/sessao - Deve abrir sessao informando duracao em segundos (201 Created)")
    void deveAbrirSessaoComDuracaoEmSegundos() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "duracaoSegundos", 30
        ));

        mockMvc.perform(post("/api/v1/pautas/1/sessao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId", is(1)))
                .andExpect(jsonPath("$.aberta", is(true)))
                .andExpect(jsonPath("$.status", is("ABERTA")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/sessao - Deve abrir sessao com 1 minuto padrao quando corpo nao informado")
    void deveAbrirSessaoComDuracaoPadraoQuandoCorpoNaoInformado() throws Exception {
        mockMvc.perform(post("/api/v1/pautas/1/sessao")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId", is(1)))
                .andExpect(jsonPath("$.aberta", is(true)))
                .andExpect(jsonPath("$.status", is("ABERTA")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/sessao - Deve retornar 400 Bad Request ao tentar abrir sessao para pauta que ja tem sessao aberta")
    void deveRetornar400AoTentarAbrirSessaoParaPautaQueJaPossuiSessaoAberta() throws Exception {
        mockMvc.perform(post("/api/v1/pautas/2/sessao")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Regra de negócio violada")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/sessao - Deve retornar 400 Bad Request ao tentar abrir sessao para pauta que ja teve sessao encerrada")
    void deveRetornar400AoTentarAbrirSessaoParaPautaQueJaPossuiSessaoEncerrada() throws Exception {
        mockMvc.perform(post("/api/v1/pautas/3/sessao")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Regra de negócio violada")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/sessao - Deve retornar 404 Not Found ao abrir sessao para pauta inexistente")
    void deveRetornar404AoAbrirSessaoParaPautaInexistente() throws Exception {
        mockMvc.perform(post("/api/v1/pautas/99999/sessao")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Recurso não encontrado")));
    }

    // =========================================================================
    // 5. Endpoints: GET /api/v1/pautas/{id}/sessao (Consulta de Sessão)
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/sessao - Deve consultar sessao aberta existente (200 OK)")
    void deveConsultarSessaoAbertaExistente() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/2/sessao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId", is(2)))
                .andExpect(jsonPath("$.aberta", is(true)))
                .andExpect(jsonPath("$.status", is("ABERTA")));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/sessao - Deve consultar sessao encerrada existente (200 OK)")
    void deveConsultarSessaoEncerradaExistente() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/3/sessao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId", is(3)))
                .andExpect(jsonPath("$.aberta", is(false)))
                .andExpect(jsonPath("$.status", is("ENCERRADA")));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/sessao - Deve retornar 404 Not Found ao consultar sessao de pauta sem sessao")
    void deveRetornar404AoConsultarSessaoDePautaSemSessao() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/1/sessao"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Recurso não encontrado")));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/sessao - Deve retornar 404 Not Found ao consultar sessao de pauta inexistente")
    void deveRetornar404AoConsultarSessaoDePautaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/99999/sessao"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Recurso não encontrado")));
    }

    // =========================================================================
    // 6. Endpoints: POST /api/v1/pautas/{id}/votos (Registro de Votos)
    // =========================================================================

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/votos - Deve registrar voto SIM com sucesso (201 Created)")
    void deveRegistrarVotoSimComSucesso() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "cpf", "12345678909",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/2/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId", is(2)))
                .andExpect(jsonPath("$.associadoCpf", is("12345678909")))
                .andExpect(jsonPath("$.voto", is("SIM")))
                .andExpect(jsonPath("$.mensagem", is("Voto registrado com sucesso.")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/votos - Deve registrar voto NAO com sucesso (201 Created)")
    void deveRegistrarVotoNaoComSucesso() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "cpf", "11144477735",
                "voto", "NAO"
        ));

        mockMvc.perform(post("/api/v1/pautas/2/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId", is(2)))
                .andExpect(jsonPath("$.associadoCpf", is("11144477735")))
                .andExpect(jsonPath("$.voto", is("NAO")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/votos - Deve retornar 409 Conflict ao votar duas vezes com mesmo CPF")
    void deveRetornar409AoTentarVotoDuplicado() throws Exception {
        // Associado 52998224725 já possui voto na Pauta 2 conforme data-test.sql
        String payload = objectMapper.writeValueAsString(Map.of(
                "cpf", "52998224725",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/2/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", is("Voto duplicado")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/votos - Deve retornar 422 Unprocessable Entity ao votar em pauta sem sessao aberta")
    void deveRetornar422AoVotarEmPautaSemSessaoAberta() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "cpf", "12345678909",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title", is("Sessão de votação indisponível")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/votos - Deve retornar 422 Unprocessable Entity ao votar em pauta com sessao encerrada")
    void deveRetornar422AoVotarEmPautaComSessaoEncerrada() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "cpf", "85387224095",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/3/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title", is("Sessão de votação indisponível")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/votos - Deve retornar 422 Unprocessable Entity quando associado estiver inapto")
    void deveRetornar422QuandoAssociadoInapto() throws Exception {
        when(cpfValidationService.validarCpfParaVotacao("85387224095"))
                .thenThrow(new AssociadoInaptoException("Associado com CPF 85387224095 não está habilitado para votar nesta pauta."));

        String payload = objectMapper.writeValueAsString(Map.of(
                "cpf", "85387224095",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/2/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title", is("Associado inapto para votação")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/votos - Deve retornar 400 Bad Request quando CPF for invalido")
    void deveRetornar400QuandoCpfInvalido() throws Exception {
        when(cpfValidationService.validarCpfParaVotacao("11111111111"))
                .thenThrow(new CpfInvalidoException("CPF informado é inválido: formato ou dígitos verificadores incorretos."));

        String payload = objectMapper.writeValueAsString(Map.of(
                "cpf", "11111111111",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/2/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("CPF inválido")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/votos - Deve retornar 400 Bad Request quando opcao de voto for invalida")
    void deveRetornar400QuandoOpcaoVotoInvalida() throws Exception {
        String payload = "{\"cpf\": \"12345678909\", \"voto\": \"TALVEZ\"}";

        mockMvc.perform(post("/api/v1/pautas/2/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Corpo da requisição inválido")));
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/votos - Deve retornar 400 Bad Request quando CPF for vazio")
    void deveRetornar400QuandoCpfVazio() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "cpf", "",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/2/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Dados de requisição inválidos")))
                .andExpect(jsonPath("$.erros.cpf").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/pautas/{id}/votos - Deve retornar 404 Not Found ao votar em pauta inexistente")
    void deveRetornar404AoVotarEmPautaInexistente() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "cpf", "12345678909",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/99999/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Recurso não encontrado")));
    }

    // =========================================================================
    // 7. Endpoints: GET /api/v1/pautas/{id}/resultado (Apuração de Resultados)
    // =========================================================================

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/resultado - Deve apurar resultado de pauta APROVADA (200 OK)")
    void deveApurarResultadoPautaAprovada() throws Exception {
        // Pauta 3 populada no data-test.sql: 2 votos SIM, 1 voto NAO -> Total 3 (APROVADA)
        mockMvc.perform(get("/api/v1/pautas/3/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId", is(3)))
                .andExpect(jsonPath("$.tituloPauta", is("Pauta Fake Encerrada Aprovada")))
                .andExpect(jsonPath("$.statusSessao", is("ENCERRADA")))
                .andExpect(jsonPath("$.totalVotos", is(3)))
                .andExpect(jsonPath("$.votosSim", is(2)))
                .andExpect(jsonPath("$.votosNao", is(1)))
                .andExpect(jsonPath("$.resultado", is("APROVADA")))
                .andExpect(jsonPath("$.descricaoResultado", is("Pauta aprovada pela maioria dos associados")));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/resultado - Deve apurar resultado de pauta REJEITADA (200 OK)")
    void deveApurarResultadoPautaRejeitada() throws Exception {
        // Pauta 4 populada no data-test.sql: 1 voto SIM, 2 votos NAO -> Total 3 (REJEITADA)
        mockMvc.perform(get("/api/v1/pautas/4/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId", is(4)))
                .andExpect(jsonPath("$.tituloPauta", is("Pauta Fake Encerrada Rejeitada")))
                .andExpect(jsonPath("$.statusSessao", is("ENCERRADA")))
                .andExpect(jsonPath("$.totalVotos", is(3)))
                .andExpect(jsonPath("$.votosSim", is(1)))
                .andExpect(jsonPath("$.votosNao", is(2)))
                .andExpect(jsonPath("$.resultado", is("REJEITADA")))
                .andExpect(jsonPath("$.descricaoResultado", is("Pauta rejeitada pela maioria dos associados")));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/resultado - Deve apurar resultado de pauta com EMPATE (200 OK)")
    void deveApurarResultadoPautaEmpate() throws Exception {
        // Pauta 5 populada no data-test.sql: 1 voto SIM, 1 voto NAO -> Total 2 (EMPATE)
        mockMvc.perform(get("/api/v1/pautas/5/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId", is(5)))
                .andExpect(jsonPath("$.tituloPauta", is("Pauta Fake Encerrada Empate")))
                .andExpect(jsonPath("$.statusSessao", is("ENCERRADA")))
                .andExpect(jsonPath("$.totalVotos", is(2)))
                .andExpect(jsonPath("$.votosSim", is(1)))
                .andExpect(jsonPath("$.votosNao", is(1)))
                .andExpect(jsonPath("$.resultado", is("EMPATE")))
                .andExpect(jsonPath("$.descricaoResultado", is("Votação empatada entre Sim e Não")));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/resultado - Deve apurar resultado de pauta SEM VOTOS (200 OK)")
    void deveApurarResultadoPautaSemVotos() throws Exception {
        // Pauta 6 populada no data-test.sql: 0 votos -> SEM_VOTOS
        mockMvc.perform(get("/api/v1/pautas/6/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId", is(6)))
                .andExpect(jsonPath("$.tituloPauta", is("Pauta Fake Encerrada Sem Votos")))
                .andExpect(jsonPath("$.statusSessao", is("ENCERRADA")))
                .andExpect(jsonPath("$.totalVotos", is(0)))
                .andExpect(jsonPath("$.votosSim", is(0)))
                .andExpect(jsonPath("$.votosNao", is(0)))
                .andExpect(jsonPath("$.resultado", is("SEM_VOTOS")))
                .andExpect(jsonPath("$.descricaoResultado", is("Nenhum voto registrado para a pauta")));
    }

    @Test
    @DisplayName("GET /api/v1/pautas/{id}/resultado - Deve retornar 404 Not Found ao apurar pauta inexistente")
    void deveRetornar404AoApurarPautaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/99999/resultado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Recurso não encontrado")));
    }

    // =========================================================================
    // 8. Fluxo E2E Integrado Completo
    // =========================================================================

    @Test
    @DisplayName("Fluxo completo E2E: Criação de nova pauta, abertura de sessão, múltiplos votos e apuração")
    void deveExecutarFluxoCompletoDeVotacao() throws Exception {
        // 1. Cadastrar pauta
        String pautaJson = objectMapper.writeValueAsString(Map.of(
                "titulo", "Reforma do Estatuto Social E2E",
                "descricao", "Votação das alterações nos artigos 5 e 12."
        ));

        String pautaResponse = mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pautaJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.titulo", is("Reforma do Estatuto Social E2E")))
                .andExpect(jsonPath("$.statusSessao", is("NAO_INICIADA")))
                .andReturn().getResponse().getContentAsString();

        Number pautaId = objectMapper.readTree(pautaResponse).get("id").numberValue();

        // 2. Tentar votar antes de abrir sessão -> Deve retornar 422
        String votoPrecoce = objectMapper.writeValueAsString(Map.of(
                "cpf", "52998224725",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(votoPrecoce))
                .andExpect(status().isUnprocessableEntity());

        // 3. Abrir sessão com 60 segundos
        String sessaoJson = objectMapper.writeValueAsString(Map.of(
                "duracaoSegundos", 60
        ));

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/sessao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessaoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId", is(pautaId.intValue())))
                .andExpect(jsonPath("$.aberta", is(true)))
                .andExpect(jsonPath("$.status", is("ABERTA")));

        // 4. Votar com associado 1 (SIM) -> 201 Created
        String voto1 = objectMapper.writeValueAsString(Map.of(
                "cpf", "52998224725",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voto1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.associadoCpf", is("52998224725")))
                .andExpect(jsonPath("$.voto", is("SIM")));

        // 5. Votar com associado 2 (SIM) -> 201 Created
        String voto2 = objectMapper.writeValueAsString(Map.of(
                "cpf", "12345678909",
                "voto", "SIM"
        ));

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voto2))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.voto", is("SIM")));

        // 6. Votar com associado 3 (NAO) -> 201 Created
        String voto3 = objectMapper.writeValueAsString(Map.of(
                "cpf", "11144477735",
                "voto", "NAO"
        ));

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voto3))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.voto", is("NAO")));

        // 7. Obter resultado da pauta -> Total=3, SIM=2, NAO=1, APROVADA
        mockMvc.perform(get("/api/v1/pautas/" + pautaId + "/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pautaId", is(pautaId.intValue())))
                .andExpect(jsonPath("$.totalVotos", is(3)))
                .andExpect(jsonPath("$.votosSim", is(2)))
                .andExpect(jsonPath("$.votosNao", is(1)))
                .andExpect(jsonPath("$.resultado", is("APROVADA")));
    }
}
