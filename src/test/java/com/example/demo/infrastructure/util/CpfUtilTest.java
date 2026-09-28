package com.example.demo.infrastructure.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfUtilTest {

    @Test
    @DisplayName("Deve validar CPFs reais válidos com ou sem pontuação")
    void deveValidarCpfsValidos() {
        // CPFs matematicamente válidos calculados pelo algoritmo oficial
        assertTrue(CpfUtil.isValido("52998224725"));
        assertTrue(CpfUtil.isValido("529.982.247-25"));
        assertTrue(CpfUtil.isValido("12345678909"));
        assertTrue(CpfUtil.isValido("11144477735"));
    }

    @Test
    @DisplayName("Deve invalidar CPFs com dígitos verificadores incorretos")
    void deveInvalidarCpfsComDigitosIncorretos() {
        assertFalse(CpfUtil.isValido("12345678901"));
        assertFalse(CpfUtil.isValido("11122233344"));
        assertFalse(CpfUtil.isValido("00000000001"));
    }

    @Test
    @DisplayName("Deve invalidar CPFs com todos os dígitos iguais")
    void deveInvalidarCpfsComDigitosIguais() {
        assertFalse(CpfUtil.isValido("00000000000"));
        assertFalse(CpfUtil.isValido("11111111111"));
        assertFalse(CpfUtil.isValido("99999999999"));
    }

    @Test
    @DisplayName("Deve invalidar CPFs nulos, vazios ou com tamanho inválido")
    void deveInvalidarCpfsComTamanhoInvalido() {
        assertFalse(CpfUtil.isValido(null));
        assertFalse(CpfUtil.isValido(""));
        assertFalse(CpfUtil.isValido("123"));
        assertFalse(CpfUtil.isValido("123456789012345"));
    }

    @Test
    @DisplayName("Deve limpar pontuação do CPF retornando apenas dígitos")
    void deveLimparPontuacaoDoCpf() {
        assertEquals("52998224725", CpfUtil.limpar("529.982.247-25"));
        assertEquals("12345678900", CpfUtil.limpar("  123.456.789-00  "));
        assertNull(CpfUtil.limpar(null));
    }
}
