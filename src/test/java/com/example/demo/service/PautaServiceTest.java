package com.example.demo.service;

import com.example.demo.domain.model.Pauta;
import com.example.demo.domain.repository.PautaRepository;
import com.example.demo.exception.EntidadeNaoEncontradaException;
import com.example.demo.web.dto.PautaRequestDTO;
import com.example.demo.web.dto.PautaResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    @DisplayName("Deve criar nova pauta com sucesso")
    void deveCriarPautaComSucesso() {
        PautaRequestDTO dto = PautaRequestDTO.builder()
                .titulo("Assembleia Geral Ordinária")
                .descricao("Prestação de contas 2025")
                .build();

        Pauta pautaSalva = Pauta.builder()
                .id(1L)
                .titulo("Assembleia Geral Ordinária")
                .descricao("Prestação de contas 2025")
                .dataCriacao(LocalDateTime.now())
                .build();

        when(pautaRepository.save(any(Pauta.class))).thenReturn(pautaSalva);

        Pauta resultado = pautaService.criarPauta(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Assembleia Geral Ordinária", resultado.getTitulo());
        verify(pautaRepository, times(1)).save(any(Pauta.class));
    }

    @Test
    @DisplayName("Deve buscar pauta por ID com sucesso")
    void deveBuscarPautaPorIdComSucesso() {
        Pauta pauta = Pauta.builder()
                .id(1L)
                .titulo("Pauta Teste")
                .descricao("Descrição Teste")
                .build();

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));

        Pauta resultado = pautaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Pauta Teste", resultado.getTitulo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pauta por ID inexistente")
    void deveLancarExcecaoQuandoPautaNaoEncontrada() {
        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntidadeNaoEncontradaException.class, () -> pautaService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve listar todas as pautas cadastradas")
    void deveListarTodasAsPautas() {
        List<Pauta> pautas = List.of(
                Pauta.builder().id(2L).titulo("Pauta 2").build(),
                Pauta.builder().id(1L).titulo("Pauta 1").build()
        );

        when(pautaRepository.findAll(any(Sort.class))).thenReturn(pautas);

        List<Pauta> resultado = pautaService.listarTodas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(pautaRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("Deve converter entidade Pauta em PautaResponseDTO corretamente")
    void deveConverterParaResponseDTO() {
        Pauta pauta = Pauta.builder()
                .id(1L)
                .titulo("Pauta de Exemplo")
                .descricao("Detalhes da pauta")
                .dataCriacao(LocalDateTime.of(2026, 9, 27, 10, 0))
                .build();

        PautaResponseDTO response = pautaService.toResponseDTO(pauta);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Pauta de Exemplo", response.getTitulo());
        assertEquals("Detalhes da pauta", response.getDescricao());
    }
}
