package com.example.demo.web.dto;

import com.example.demo.domain.model.StatusSessao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representação detalhada de uma pauta")
public class PautaResponseDTO {

    @Schema(description = "Identificador único da pauta", example = "1")
    private Long id;

    @Schema(description = "Título da pauta", example = "Aprovação do Orçamento Anual 2026")
    private String titulo;

    @Schema(description = "Descrição da pauta", example = "Deliberação sobre os investimentos prioritários para o exercício de 2026.")
    private String descricao;

    @Schema(description = "Data e hora de criação da pauta", example = "2026-09-27T10:00:00")
    private LocalDateTime dataCriacao;

    @Schema(description = "Status atual da sessão de votação da pauta", example = "ABERTA")
    private StatusSessao statusSessao;
}
