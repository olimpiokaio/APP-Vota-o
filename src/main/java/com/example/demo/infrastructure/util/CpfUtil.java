package com.example.demo.infrastructure.util;

public final class CpfUtil {

    private CpfUtil() {
    }

    public static String limpar(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("\\D", "");
    }

    public static boolean isValido(String cpf) {
        String limpo = limpar(cpf);
        if (limpo == null || limpo.length() != 11) {
            return false;
        }

        // Verifica CPFs com todos os dígitos iguais (ex: 00000000000, 11111111111)
        if (limpo.chars().distinct().count() == 1) {
            return false;
        }

        try {
            // Calcula 1º dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                int digito = Character.getNumericValue(limpo.charAt(i));
                soma += digito * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) {
                primeiroDigito = 0;
            }
            if (primeiroDigito != Character.getNumericValue(limpo.charAt(9))) {
                return false;
            }

            // Calcula 2º dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                int digito = Character.getNumericValue(limpo.charAt(i));
                soma += digito * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) {
                segundoDigito = 0;
            }
            return segundoDigito == Character.getNumericValue(limpo.charAt(10));

        } catch (Exception e) {
            return false;
        }
    }
}
