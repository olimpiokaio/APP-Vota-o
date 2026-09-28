package com.example.demo.infrastructure.client;

import com.example.demo.exception.AssociadoInaptoException;
import com.example.demo.exception.CpfInvalidoException;
import com.example.demo.infrastructure.client.dto.CpfStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CpfValidationServiceImplTest {

    @Test
    @DisplayName("Deve lançar CpfInvalidoException quando CPF for matematicamente inválido")
    void deveLancarExcecaoQuandoCpfInvalido() {
        CpfValidationServiceImpl service = new CpfValidationServiceImpl();

        assertThrows(CpfInvalidoException.class, () -> service.validarCpfParaVotacao("11111111111"));
        assertThrows(CpfInvalidoException.class, () -> service.validarCpfParaVotacao("12345678900"));
    }

    @Test
    @DisplayName("Deve retornar ABLE_TO_VOTE quando sorteio for menor que 80")
    void deveRetornarAbleToVoteQuandoSorteioFavoravel() {
        Random mockRandom = mock(Random.class);
        when(mockRandom.nextInt(100)).thenReturn(0, 50, 79);

        CpfValidationServiceImpl service = new CpfValidationServiceImpl(mockRandom);

        assertEquals(CpfStatus.ABLE_TO_VOTE, service.validarCpfParaVotacao("52998224725"));
        assertEquals(CpfStatus.ABLE_TO_VOTE, service.validarCpfParaVotacao("52998224725"));
        assertEquals(CpfStatus.ABLE_TO_VOTE, service.validarCpfParaVotacao("52998224725"));
    }

    @Test
    @DisplayName("Deve lançar AssociadoInaptoException quando sorteio for maior ou igual a 80")
    void deveLancarAssociadoInaptoQuandoSorteioDesfavoravel() {
        Random mockRandom = mock(Random.class);
        when(mockRandom.nextInt(100)).thenReturn(80, 99);

        CpfValidationServiceImpl service = new CpfValidationServiceImpl(mockRandom);

        assertThrows(AssociadoInaptoException.class, () -> service.validarCpfParaVotacao("52998224725"));
        assertThrows(AssociadoInaptoException.class, () -> service.validarCpfParaVotacao("52998224725"));
    }
}
