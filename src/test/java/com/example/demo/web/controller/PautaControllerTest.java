package com.example.demo.web.controller;

import com.example.demo.domain.model.OpcaoVoto;
import com.example.demo.domain.model.ResultadoVotacao;
import com.example.demo.domain.model.StatusSessao;
import com.example.demo.facade.PautaFacade;
import com.example.demo.web.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaControllerTest {

    @Mock
    private PautaFacade pautaFacade;

    @InjectMocks
    private PautaController pautaController;

    @Test
    @DisplayName("PautaController - Deve criar pauta chamando pautaFacade e retornar 201 Created")
    void deveCriarPauta() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PautaRequestDTO requestDTO = PautaRequestDTO.builder().titulo("Pauta Teste").descricao("Descricao").build();
        PautaResponseDTO responseDTO = PautaResponseDTO.builder()
                .id(1L)
                .titulo("Pauta Teste")
                .descricao("Descricao")
                .statusSessao(StatusSessao.NAO_INICIADA)
                .dataCriacao(LocalDateTime.now())
                .build();

        when(pautaFacade.criarPauta(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<PautaResponseDTO> response = pautaController.criarPauta(requestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(pautaFacade, times(1)).criarPauta(requestDTO);
    }

    @Test
    @DisplayName("PautaController - Deve listar todas as pautas chamando pautaFacade e retornar 200 OK")
    void deveListarTodas() {
        PautaResponseDTO responseDTO = PautaResponseDTO.builder()
                .id(1L)
                .titulo("Pauta Teste")
                .statusSessao(StatusSessao.NAO_INICIADA)
                .build();

        when(pautaFacade.listarTodas()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<PautaResponseDTO>> response = pautaController.listarTodas();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(pautaFacade, times(1)).listarTodas();
    }

    @Test
    @DisplayName("PautaController - Deve buscar pauta por id chamando pautaFacade e retornar 200 OK")
    void deveBuscarPorId() {
        PautaResponseDTO responseDTO = PautaResponseDTO.builder()
                .id(1L)
                .titulo("Pauta Teste")
                .statusSessao(StatusSessao.NAO_INICIADA)
                .build();

        when(pautaFacade.buscarPorId(1L)).thenReturn(responseDTO);

        ResponseEntity<PautaResponseDTO> response = pautaController.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(pautaFacade, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("PautaController - Deve abrir sessao chamando pautaFacade e retornar 201 Created")
    void deveAbrirSessao() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        SessaoRequestDTO requestDTO = SessaoRequestDTO.builder().duracaoMinutos(2).build();
        SessaoResponseDTO responseDTO = SessaoResponseDTO.builder()
                .id(10L)
                .pautaId(1L)
                .aberta(true)
                .status(StatusSessao.ABERTA)
                .build();

        when(pautaFacade.abrirSessao(eq(1L), any(SessaoRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<SessaoResponseDTO> response = pautaController.abrirSessao(1L, requestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(pautaFacade, times(1)).abrirSessao(1L, requestDTO);
    }

    @Test
    @DisplayName("PautaController - Deve buscar sessao chamando pautaFacade e retornar 200 OK")
    void deveBuscarSessao() {
        SessaoResponseDTO responseDTO = SessaoResponseDTO.builder()
                .id(10L)
                .pautaId(1L)
                .aberta(true)
                .status(StatusSessao.ABERTA)
                .build();

        when(pautaFacade.buscarSessao(1L)).thenReturn(responseDTO);

        ResponseEntity<SessaoResponseDTO> response = pautaController.buscarSessao(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(pautaFacade, times(1)).buscarSessao(1L);
    }

    @Test
    @DisplayName("PautaController - Deve registrar voto chamando pautaFacade e retornar 201 Created")
    void deveVotar() {
        VotoRequestDTO requestDTO = VotoRequestDTO.builder().cpf("12345678909").voto(OpcaoVoto.SIM).build();
        VotoResponseDTO responseDTO = VotoResponseDTO.builder()
                .id(100L)
                .pautaId(1L)
                .associadoCpf("12345678909")
                .voto(OpcaoVoto.SIM)
                .mensagem("Voto registrado com sucesso.")
                .build();

        when(pautaFacade.votar(1L, requestDTO)).thenReturn(responseDTO);

        ResponseEntity<VotoResponseDTO> response = pautaController.votar(1L, requestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(pautaFacade, times(1)).votar(1L, requestDTO);
    }

    @Test
    @DisplayName("PautaController - Deve obter resultado chamando pautaFacade e retornar 200 OK")
    void deveObterResultado() {
        ResultadoResponseDTO responseDTO = ResultadoResponseDTO.builder()
                .pautaId(1L)
                .tituloPauta("Pauta Teste")
                .resultado(ResultadoVotacao.APROVADA)
                .totalVotos(3L)
                .votosSim(2L)
                .votosNao(1L)
                .build();

        when(pautaFacade.obterResultado(1L)).thenReturn(responseDTO);

        ResponseEntity<ResultadoResponseDTO> response = pautaController.obterResultado(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(pautaFacade, times(1)).obterResultado(1L);
    }
}
