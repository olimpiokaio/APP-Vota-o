package com.example.demo.service;

import com.example.demo.domain.model.*;
import com.example.demo.domain.repository.VotoRepository;
import com.example.demo.exception.AssociadoInaptoException;
import com.example.demo.exception.CpfInvalidoException;
import com.example.demo.exception.SessaoNaoAbertaException;
import com.example.demo.exception.VotoDuplicadoException;
import com.example.demo.infrastructure.client.dto.CpfStatus;
import com.example.demo.web.dto.ResultadoResponseDTO;
import com.example.demo.web.dto.VotoRequestDTO;
import com.example.demo.web.dto.VotoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotacaoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaService pautaService;

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    @Mock
    private CpfValidationService cpfValidationService;

    @InjectMocks
    private VotacaoService votacaoService;

    private Pauta pauta;
    private SessaoVotacao sessaoAberta;

    @BeforeEach
    void setUp() {
        pauta = Pauta.builder()
                .id(1L)
                .titulo("Pauta de Teste")
                .descricao("Descrição da pauta")
                .dataCriacao(LocalDateTime.now())
                .build();

        sessaoAberta = SessaoVotacao.builder()
                .id(10L)
                .pauta(pauta)
                .dataHoraInicio(LocalDateTime.now().minusSeconds(10))
                .dataHoraFim(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    @Test
    @DisplayName("Deve registrar voto com sucesso")
    void deveRegistrarVotoComSucesso() {
        VotoRequestDTO dto = VotoRequestDTO.builder()
                .cpf("52998224725")
                .voto(OpcaoVoto.SIM)
                .build();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessaoAberta);
        when(cpfValidationService.validarCpfParaVotacao("52998224725")).thenReturn(CpfStatus.ABLE_TO_VOTE);
        when(votoRepository.existsByPautaIdAndAssociadoCpf(1L, "52998224725")).thenReturn(false);

        Voto votoSalvo = Voto.builder()
                .id(100L)
                .pauta(pauta)
                .associadoCpf("52998224725")
                .opcaoVoto(OpcaoVoto.SIM)
                .dataHoraVoto(LocalDateTime.now())
                .build();

        when(votoRepository.saveAndFlush(any(Voto.class))).thenReturn(votoSalvo);

        VotoResponseDTO response = votacaoService.registrarVoto(1L, dto);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getPautaId());
        assertEquals("52998224725", response.getAssociadoCpf());
        assertEquals(OpcaoVoto.SIM, response.getVoto());
        verify(votoRepository, times(1)).saveAndFlush(any(Voto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a sessão de votação estiver encerrada")
    void deveLancarExcecaoQuandoSessaoEncerrada() {
        SessaoVotacao sessaoEncerrada = SessaoVotacao.builder()
                .id(10L)
                .pauta(pauta)
                .dataHoraInicio(LocalDateTime.now().minusMinutes(10))
                .dataHoraFim(LocalDateTime.now().minusMinutes(5))
                .build();

        VotoRequestDTO dto = VotoRequestDTO.builder()
                .cpf("52998224725")
                .voto(OpcaoVoto.SIM)
                .build();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessaoEncerrada);

        assertThrows(SessaoNaoAbertaException.class, () -> votacaoService.registrarVoto(1L, dto));
        verify(votoRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o associado já votou na pauta")
    void deveLancarExcecaoQuandoVotoDuplicado() {
        VotoRequestDTO dto = VotoRequestDTO.builder()
                .cpf("52998224725")
                .voto(OpcaoVoto.SIM)
                .build();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessaoAberta);
        when(cpfValidationService.validarCpfParaVotacao("52998224725")).thenReturn(CpfStatus.ABLE_TO_VOTE);
        when(votoRepository.existsByPautaIdAndAssociadoCpf(1L, "52998224725")).thenReturn(true);

        assertThrows(VotoDuplicadoException.class, () -> votacaoService.registrarVoto(1L, dto));
        verify(votoRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando associado for inapto para votar")
    void deveLancarExcecaoQuandoAssociadoInapto() {
        VotoRequestDTO dto = VotoRequestDTO.builder()
                .cpf("52998224725")
                .voto(OpcaoVoto.SIM)
                .build();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessaoAberta);
        doThrow(new AssociadoInaptoException("Associado inapto para votar"))
                .when(cpfValidationService).validarCpfParaVotacao("52998224725");

        assertThrows(AssociadoInaptoException.class, () -> votacaoService.registrarVoto(1L, dto));
        verify(votoRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve apurar resultado como APROVADA quando votos SIM superarem NAO")
    void deveApurarResultadoAprovada() {
        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.obterStatusSessao(pauta)).thenReturn(StatusSessao.ENCERRADA);
        when(votoRepository.countByPautaId(1L)).thenReturn(10L);
        when(votoRepository.countByPautaIdAndOpcaoVoto(1L, OpcaoVoto.SIM)).thenReturn(7L);
        when(votoRepository.countByPautaIdAndOpcaoVoto(1L, OpcaoVoto.NAO)).thenReturn(3L);

        ResultadoResponseDTO resultado = votacaoService.apurarResultado(1L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getTotalVotos());
        assertEquals(7L, resultado.getVotosSim());
        assertEquals(3L, resultado.getVotosNao());
        assertEquals(ResultadoVotacao.APROVADA, resultado.getResultado());
    }

    @Test
    @DisplayName("Deve apurar resultado como REJEITADA quando votos NAO superarem SIM")
    void deveApurarResultadoRejeitada() {
        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.obterStatusSessao(pauta)).thenReturn(StatusSessao.ENCERRADA);
        when(votoRepository.countByPautaId(1L)).thenReturn(5L);
        when(votoRepository.countByPautaIdAndOpcaoVoto(1L, OpcaoVoto.SIM)).thenReturn(2L);
        when(votoRepository.countByPautaIdAndOpcaoVoto(1L, OpcaoVoto.NAO)).thenReturn(3L);

        ResultadoResponseDTO resultado = votacaoService.apurarResultado(1L);

        assertNotNull(resultado);
        assertEquals(ResultadoVotacao.REJEITADA, resultado.getResultado());
    }

    @Test
    @DisplayName("Deve apurar resultado como EMPATE quando votos SIM forem iguais a NAO")
    void deveApurarResultadoEmpate() {
        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.obterStatusSessao(pauta)).thenReturn(StatusSessao.ENCERRADA);
        when(votoRepository.countByPautaId(1L)).thenReturn(4L);
        when(votoRepository.countByPautaIdAndOpcaoVoto(1L, OpcaoVoto.SIM)).thenReturn(2L);
        when(votoRepository.countByPautaIdAndOpcaoVoto(1L, OpcaoVoto.NAO)).thenReturn(2L);

        ResultadoResponseDTO resultado = votacaoService.apurarResultado(1L);

        assertNotNull(resultado);
        assertEquals(ResultadoVotacao.EMPATE, resultado.getResultado());
    }

    @Test
    @DisplayName("Deve apurar resultado como SEM_VOTOS quando não houver votos")
    void deveApurarResultadoSemVotos() {
        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.obterStatusSessao(pauta)).thenReturn(StatusSessao.NAO_INICIADA);
        when(votoRepository.countByPautaId(1L)).thenReturn(0L);
        when(votoRepository.countByPautaIdAndOpcaoVoto(1L, OpcaoVoto.SIM)).thenReturn(0L);
        when(votoRepository.countByPautaIdAndOpcaoVoto(1L, OpcaoVoto.NAO)).thenReturn(0L);

        ResultadoResponseDTO resultado = votacaoService.apurarResultado(1L);

        assertNotNull(resultado);
        assertEquals(ResultadoVotacao.SEM_VOTOS, resultado.getResultado());
    }
}
