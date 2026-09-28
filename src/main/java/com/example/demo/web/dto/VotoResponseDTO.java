package com.example.demo.web.dto;

import com.example.demo.domain.model.OpcaoVoto;
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
@Schema(description = "Confirmação de recebimento do voto")
public class VotoResponseDTO {

    @Schema(description = "Identificador do voto registrado", example = "1")
    private Long id;

    @Schema(description = "Identificador da pauta votada", example = "1")
    private Long pautaId;

    @Schema(description = "CPF do associado", example = "03569852003")
    private String associadoCpf;

    @Schema(description = "Opção de voto registrada", example = "SIM")
    private OpcaoVoto voto;

    @Schema(description = "Data e hora do registro do voto", example = "2026-09-27T23:10:15")
    private LocalDateTime dataHoraVoto;

    @Schema(description = "Mensagem descritiva", example = "Voto registrado com sucesso.")
    private String mensagem;
}
