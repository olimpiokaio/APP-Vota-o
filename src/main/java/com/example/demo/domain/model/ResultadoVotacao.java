package com.example.demo.domain.model;

public enum ResultadoVotacao {
    APROVADA("Pauta aprovada pela maioria dos associados"),
    REJEITADA("Pauta rejeitada pela maioria dos associados"),
    EMPATE("Votação empatada entre Sim e Não"),
    SEM_VOTOS("Nenhum voto registrado para a pauta");

    private final String descricao;

    ResultadoVotacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
