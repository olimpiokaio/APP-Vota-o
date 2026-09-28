package com.example.demo.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Parâmetros para abertura da sessão de votação")
public class SessaoRequestDTO {

    @Positive(message = "A duração em minutos deve ser um número positivo.")
    @Schema(description = "Duração da sessão em minutos (padrão: 1 minuto caso não informado)", example = "1")
    private Integer duracaoMinutos;

    @Positive(message = "A duração em segundos deve ser um número positivo.")
    @Schema(description = "Duração da sessão em segundos (opcional, sobrepõe minutos se informado)", example = "60")
    private Long duracaoSegundos;
}
