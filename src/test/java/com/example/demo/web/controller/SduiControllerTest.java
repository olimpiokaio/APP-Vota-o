package com.example.demo.web.controller;

import com.example.demo.facade.SduiFacade;
import com.example.demo.web.dto.sdui.SduiTelaDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SduiControllerTest {

    @Mock
    private SduiFacade sduiFacade;

    @InjectMocks
    private SduiController sduiController;

    @Test
    @DisplayName("SduiController - Deve obter tela de cadastro de pauta chamando sduiFacade e retornar 200 OK")
    void deveObterTelaCadastroPauta() {
        SduiTelaDTO mockTela = SduiTelaDTO.builder().tipo("FORMULARIO").titulo("Cadastrar Nova Pauta").build();
        when(sduiFacade.obterTelaCadastroPauta()).thenReturn(mockTela);

        ResponseEntity<SduiTelaDTO> response = sduiController.telaCadastroPauta();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTela, response.getBody());
        verify(sduiFacade, times(1)).obterTelaCadastroPauta();
    }

    @Test
    @DisplayName("SduiController - Deve obter tela de seleção de pautas chamando sduiFacade e retornar 200 OK")
    void deveObterTelaSelecaoPautas() {
        SduiTelaDTO mockTela = SduiTelaDTO.builder().tipo("SELECAO").titulo("Pautas Disponíveis").build();
        when(sduiFacade.obterTelaSelecaoPautas()).thenReturn(mockTela);

        ResponseEntity<SduiTelaDTO> response = sduiController.telaSelecaoPautas();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTela, response.getBody());
        verify(sduiFacade, times(1)).obterTelaSelecaoPautas();
    }

    @Test
    @DisplayName("SduiController - Deve obter tela de abertura de sessão chamando sduiFacade e retornar 200 OK")
    void deveObterTelaAberturaSessao() {
        SduiTelaDTO mockTela = SduiTelaDTO.builder().tipo("FORMULARIO").titulo("Abrir Sessão de Votação").build();
        when(sduiFacade.obterTelaAberturaSessao(1L)).thenReturn(mockTela);

        ResponseEntity<SduiTelaDTO> response = sduiController.telaAberturaSessao(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTela, response.getBody());
        verify(sduiFacade, times(1)).obterTelaAberturaSessao(1L);
    }

    @Test
    @DisplayName("SduiController - Deve obter tela de votação chamando sduiFacade e retornar 200 OK")
    void deveObterTelaVotacao() {
        SduiTelaDTO mockTela = SduiTelaDTO.builder().tipo("FORMULARIO").titulo("Votar na Pauta").build();
        when(sduiFacade.obterTelaVotacao(1L)).thenReturn(mockTela);

        ResponseEntity<SduiTelaDTO> response = sduiController.telaVotacao(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTela, response.getBody());
        verify(sduiFacade, times(1)).obterTelaVotacao(1L);
    }
}
