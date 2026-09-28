package com.example.demo.service;

import com.example.demo.domain.model.Pauta;
import com.example.demo.domain.model.SessaoVotacao;
import com.example.demo.domain.model.StatusSessao;
import com.example.demo.domain.repository.SessaoVotacaoRepository;
import com.example.demo.exception.RegraDeNegocioException;
import com.example.demo.web.dto.SessaoRequestDTO;
import com.example.demo.web.dto.SessaoResponseDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private SessaoVotacaoService sessaoVotacaoService;

    private Pauta pauta;

    @BeforeEach
    void setUp() {
        pauta = Pauta.builder()
                .id(1L)
                .titulo("Pauta Teste")
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve abrir sessão com duração padrão de 1 minuto quando não especificado")
    void deveAbrirSessaoComDuracaoPadrao() {
        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoRepository.existsByPautaId(1L)).thenReturn(false);
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenAnswer(i -> {
            SessaoVotacao s = i.getArgument(0);
            s.setId(10L);
            return s;
        });

        SessaoVotacao sessao = sessaoVotacaoService.abrirSessao(1L, null);

        assertNotNull(sessao);
        assertEquals(10L, sessao.getId());
        assertEquals(pauta, sessao.getPauta());
        assertTrue(sessao.getDataHoraFim().isAfter(sessao.getDataHoraInicio()));
        assertTrue(sessao.isAberta());
        verify(sessaoVotacaoRepository, times(1)).save(any(SessaoVotacao.class));
    }

    @Test
    @DisplayName("Deve abrir sessão com duração personalizada em minutos")
    void deveAbrirSessaoComDuracaoEmMinutos() {
        SessaoRequestDTO dto = SessaoRequestDTO.builder().duracaoMinutos(15).build();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoRepository.existsByPautaId(1L)).thenReturn(false);
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenAnswer(i -> i.getArgument(0));

        SessaoVotacao sessao = sessaoVotacaoService.abrirSessao(1L, dto);

        assertNotNull(sessao);
        assertTrue(sessao.getDataHoraFim().isAfter(sessao.getDataHoraInicio().plusMinutes(14)));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar abrir sessão para pauta que já possui sessão")
    void deveLancarExcecaoQuandoSessaoJaExiste() {
        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoRepository.existsByPautaId(1L)).thenReturn(true);

        assertThrows(RegraDeNegocioException.class, () -> sessaoVotacaoService.abrirSessao(1L, null));
        verify(sessaoVotacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve converter SessaoVotacao para SessaoResponseDTO com status correto")
    void deveConverterParaResponseDTO() {
        LocalDateTime inicio = LocalDateTime.now().minusSeconds(10);
        LocalDateTime fim = LocalDateTime.now().plusMinutes(5);

        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(1L)
                .pauta(pauta)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .build();

        SessaoResponseDTO response = sessaoVotacaoService.toResponseDTO(sessao);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getPautaId());
        assertTrue(response.isAberta());
        assertEquals(StatusSessao.ABERTA, response.getStatus());
    }
}
