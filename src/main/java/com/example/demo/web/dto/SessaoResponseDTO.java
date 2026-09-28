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
@Schema(description = "Informações da sessão de votação aberta")
public class SessaoResponseDTO {

    @Schema(description = "Identificador da sessão", example = "1")
    private Long id;

    @Schema(description = "Identificador da pauta vinculada", example = "1")
    private Long pautaId;

    @Schema(description = "Data e hora de abertura da sessão", example = "2026-09-27T23:10:00")
    private LocalDateTime dataHoraInicio;

    @Schema(description = "Data e hora prevista para encerramento da sessão", example = "2026-09-27T23:11:00")
    private LocalDateTime dataHoraFim;

    @Schema(description = "Indica se a sessão está aberta para votação no momento da consulta", example = "true")
    private boolean aberta;

    @Schema(description = "Status detalhado da sessão", example = "ABERTA")
    private StatusSessao status;
}
