package com.example.demo.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OpcaoVoto {
    SIM,
    NAO;

    @JsonCreator
    public static OpcaoVoto fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        if ("SIM".equals(normalized) || "S".equals(normalized) || "YES".equals(normalized) || "Y".equals(normalized) || "TRUE".equals(normalized)) {
            return SIM;
        }
        if ("NAO".equals(normalized) || "NÃO".equals(normalized) || "N".equals(normalized) || "NO".equals(normalized) || "FALSE".equals(normalized)) {
            return NAO;
        }
        throw new IllegalArgumentException("Opção de voto inválida: '" + value + "'. Valores aceitos: SIM, NAO.");
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
