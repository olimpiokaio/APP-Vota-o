package com.example.demo.web.dto;

import com.example.demo.domain.model.ResultadoVotacao;
import com.example.demo.domain.model.StatusSessao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resultado consolidado da apuração dos votos da pauta")
public class ResultadoResponseDTO {

    @Schema(description = "Identificador da pauta", example = "1")
    private Long pautaId;

    @Schema(description = "Título da pauta", example = "Aprovação do Orçamento Anual 2026")
    private String tituloPauta;

    @Schema(description = "Descrição da pauta", example = "Deliberação sobre os investimentos prioritários para o exercício de 2026.")
    private String descricaoPauta;

    @Schema(description = "Status atual da sessão de votação", example = "ENCERRADA")
    private StatusSessao statusSessao;

    @Schema(description = "Total de votos computados", example = "150")
    private long totalVotos;

    @Schema(description = "Total de votos favoráveis (SIM)", example = "100")
    private long votosSim;

    @Schema(description = "Total de votos contrários (NAO)", example = "50")
    private long votosNao;

    @Schema(description = "Resultado apurado da votação", example = "APROVADA")
    private ResultadoVotacao resultado;

    @Schema(description = "Descrição textual do resultado", example = "Pauta aprovada pela maioria dos associados")
    private String descricaoResultado;
}
