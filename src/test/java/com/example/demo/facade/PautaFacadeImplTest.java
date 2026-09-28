package com.example.demo.facade;

import com.example.demo.domain.model.OpcaoVoto;
import com.example.demo.domain.model.Pauta;
import com.example.demo.domain.model.ResultadoVotacao;
import com.example.demo.domain.model.SessaoVotacao;
import com.example.demo.domain.model.StatusSessao;
import com.example.demo.facade.impl.PautaFacadeImpl;
import com.example.demo.service.PautaService;
import com.example.demo.service.SessaoVotacaoService;
import com.example.demo.service.VotacaoService;
import com.example.demo.web.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaFacadeImplTest {

    @Mock
    private PautaService pautaService;

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    @Mock
    private VotacaoService votacaoService;

    @InjectMocks
    private PautaFacadeImpl pautaFacade;

    private Pauta pauta;
    private PautaResponseDTO pautaResponseDTO;
    private SessaoVotacao sessaoVotacao;
    private SessaoResponseDTO sessaoResponseDTO;

    @BeforeEach
    void setUp() {
        pauta = Pauta.builder()
                .id(1L)
                .titulo("Pauta Teste Facade")
                .descricao("Descrição Facade")
                .dataCriacao(LocalDateTime.now())
                .build();

        pautaResponseDTO = PautaResponseDTO.builder()
                .id(1L)
                .titulo("Pauta Teste Facade")
                .descricao("Descrição Facade")
                .dataCriacao(pauta.getDataCriacao())
                .statusSessao(StatusSessao.NAO_INICIADA)
                .build();

        sessaoVotacao = SessaoVotacao.builder()
                .id(10L)
                .pauta(pauta)
                .dataHoraInicio(LocalDateTime.now())
                .dataHoraFim(LocalDateTime.now().plusMinutes(1))
                .build();

        sessaoResponseDTO = SessaoResponseDTO.builder()
                .id(10L)
                .pautaId(1L)
                .dataHoraInicio(sessaoVotacao.getDataHoraInicio())
                .dataHoraFim(sessaoVotacao.getDataHoraFim())
                .aberta(true)
                .status(StatusSessao.ABERTA)
                .build();
    }

    @Test
    @DisplayName("PautaFacade - Deve criar pauta delegando para PautaService")
    void deveCriarPauta() {
        PautaRequestDTO dto = PautaRequestDTO.builder().titulo("Pauta Teste Facade").descricao("Descrição Facade").build();

        when(pautaService.criarPauta(dto)).thenReturn(pauta);
        when(pautaService.toResponseDTO(pauta)).thenReturn(pautaResponseDTO);

        PautaResponseDTO resultado = pautaFacade.criarPauta(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Pauta Teste Facade", resultado.getTitulo());
        verify(pautaService, times(1)).criarPauta(dto);
        verify(pautaService, times(1)).toResponseDTO(pauta);
    }

    @Test
    @DisplayName("PautaFacade - Deve listar todas as pautas")
    void deveListarTodasAsPautas() {
        when(pautaService.listarTodas()).thenReturn(List.of(pauta));
        when(pautaService.toResponseDTO(pauta)).thenReturn(pautaResponseDTO);

        List<PautaResponseDTO> resultado = pautaFacade.listarTodas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        verify(pautaService, times(1)).listarTodas();
        verify(pautaService, times(1)).toResponseDTO(pauta);
    }

    @Test
    @DisplayName("PautaFacade - Deve buscar pauta por ID")
    void deveBuscarPorId() {
        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(pautaService.toResponseDTO(pauta)).thenReturn(pautaResponseDTO);

        PautaResponseDTO resultado = pautaFacade.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(pautaService, times(1)).buscarPorId(1L);
        verify(pautaService, times(1)).toResponseDTO(pauta);
    }

    @Test
    @DisplayName("PautaFacade - Deve abrir sessão delegando para SessaoVotacaoService")
    void deveAbrirSessao() {
        SessaoRequestDTO dto = SessaoRequestDTO.builder().duracaoMinutos(5).build();

        when(sessaoVotacaoService.abrirSessao(1L, dto)).thenReturn(sessaoVotacao);
        when(sessaoVotacaoService.toResponseDTO(sessaoVotacao)).thenReturn(sessaoResponseDTO);

        SessaoResponseDTO resultado = pautaFacade.abrirSessao(1L, dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(1L, resultado.getPautaId());
        assertTrue(resultado.isAberta());
        verify(sessaoVotacaoService, times(1)).abrirSessao(1L, dto);
        verify(sessaoVotacaoService, times(1)).toResponseDTO(sessaoVotacao);
    }

    @Test
    @DisplayName("PautaFacade - Deve buscar sessão por pauta ID")
    void deveBuscarSessao() {
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessaoVotacao);
        when(sessaoVotacaoService.toResponseDTO(sessaoVotacao)).thenReturn(sessaoResponseDTO);

        SessaoResponseDTO resultado = pautaFacade.buscarSessao(1L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        verify(sessaoVotacaoService, times(1)).buscarPorPautaId(1L);
        verify(sessaoVotacaoService, times(1)).toResponseDTO(sessaoVotacao);
    }

    @Test
    @DisplayName("PautaFacade - Deve registrar voto delegando para VotacaoService")
    void deveRegistrarVoto() {
        VotoRequestDTO dto = VotoRequestDTO.builder().cpf("12345678909").voto(OpcaoVoto.SIM).build();
        VotoResponseDTO votoResponse = VotoResponseDTO.builder()
                .id(100L)
                .pautaId(1L)
                .associadoCpf("12345678909")
                .voto(OpcaoVoto.SIM)
                .dataHoraVoto(LocalDateTime.now())
                .mensagem("Voto registrado com sucesso.")
                .build();

        when(votacaoService.registrarVoto(1L, dto)).thenReturn(votoResponse);

        VotoResponseDTO resultado = pautaFacade.votar(1L, dto);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals(1L, resultado.getPautaId());
        verify(votacaoService, times(1)).registrarVoto(1L, dto);
    }

    @Test
    @DisplayName("PautaFacade - Deve apurar resultado delegando para VotacaoService")
    void deveApurarResultado() {
        ResultadoResponseDTO resultadoEsperado = ResultadoResponseDTO.builder()
                .pautaId(1L)
                .tituloPauta("Pauta Teste Facade")
                .statusSessao(StatusSessao.ENCERRADA)
                .totalVotos(5L)
                .votosSim(4L)
                .votosNao(1L)
                .resultado(ResultadoVotacao.APROVADA)
                .descricaoResultado("Pauta aprovada pela maioria dos associados")
                .build();

        when(votacaoService.apurarResultado(1L)).thenReturn(resultadoEsperado);

        ResultadoResponseDTO resultado = pautaFacade.obterResultado(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getPautaId());
        assertEquals(ResultadoVotacao.APROVADA, resultado.getResultado());
        assertEquals(5L, resultado.getTotalVotos());
        verify(votacaoService, times(1)).apurarResultado(1L);
    }
}
