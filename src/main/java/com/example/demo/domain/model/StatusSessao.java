package com.example.demo.domain.model;

public enum StatusSessao {
    NAO_INICIADA("Sessão não iniciada"),
    ABERTA("Sessão aberta para votação"),
    ENCERRADA("Sessão encerrada");

    private final String descricao;

    StatusSessao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
