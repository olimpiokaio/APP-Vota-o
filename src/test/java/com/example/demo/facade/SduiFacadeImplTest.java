package com.example.demo.facade;

import com.example.demo.facade.impl.SduiFacadeImpl;
import com.example.demo.service.SduiService;
import com.example.demo.web.dto.sdui.SduiTelaDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SduiFacadeImplTest {

    @Mock
    private SduiService sduiService;

    @InjectMocks
    private SduiFacadeImpl sduiFacade;

    @Test
    @DisplayName("SduiFacade - Deve obter tela de cadastro de pauta delegando para SduiService")
    void deveObterTelaCadastroPauta() {
        SduiTelaDTO mockTela = SduiTelaDTO.builder().tipo("FORMULARIO").titulo("Cadastrar Nova Pauta").build();
        when(sduiService.obterTelaCadastroPauta()).thenReturn(mockTela);

        SduiTelaDTO resultado = sduiFacade.obterTelaCadastroPauta();

        assertNotNull(resultado);
        assertEquals("FORMULARIO", resultado.getTipo());
        assertEquals("Cadastrar Nova Pauta", resultado.getTitulo());
        verify(sduiService, times(1)).obterTelaCadastroPauta();
    }

    @Test
    @DisplayName("SduiFacade - Deve obter tela de seleção de pautas delegando para SduiService")
    void deveObterTelaSelecaoPautas() {
        SduiTelaDTO mockTela = SduiTelaDTO.builder().tipo("SELECAO").titulo("Pautas Disponíveis").build();
        when(sduiService.obterTelaSelecaoPautas()).thenReturn(mockTela);

        SduiTelaDTO resultado = sduiFacade.obterTelaSelecaoPautas();

        assertNotNull(resultado);
        assertEquals("SELECAO", resultado.getTipo());
        assertEquals("Pautas Disponíveis", resultado.getTitulo());
        verify(sduiService, times(1)).obterTelaSelecaoPautas();
    }

    @Test
    @DisplayName("SduiFacade - Deve obter tela de abertura de sessão delegando para SduiService")
    void deveObterTelaAberturaSessao() {
        SduiTelaDTO mockTela = SduiTelaDTO.builder().tipo("FORMULARIO").titulo("Abrir Sessão de Votação").build();
        when(sduiService.obterTelaAberturaSessao(1L)).thenReturn(mockTela);

        SduiTelaDTO resultado = sduiFacade.obterTelaAberturaSessao(1L);

        assertNotNull(resultado);
        assertEquals("FORMULARIO", resultado.getTipo());
        assertEquals("Abrir Sessão de Votação", resultado.getTitulo());
        verify(sduiService, times(1)).obterTelaAberturaSessao(1L);
    }

    @Test
    @DisplayName("SduiFacade - Deve obter tela de votação delegando para SduiService")
    void deveObterTelaVotacao() {
        SduiTelaDTO mockTela = SduiTelaDTO.builder().tipo("FORMULARIO").titulo("Votar na Pauta").build();
        when(sduiService.obterTelaVotacao(1L)).thenReturn(mockTela);

        SduiTelaDTO resultado = sduiFacade.obterTelaVotacao(1L);

        assertNotNull(resultado);
        assertEquals("FORMULARIO", resultado.getTipo());
        assertEquals("Votar na Pauta", resultado.getTitulo());
        verify(sduiService, times(1)).obterTelaVotacao(1L);
    }
}
