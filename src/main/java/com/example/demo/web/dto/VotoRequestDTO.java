package com.example.demo.web.dto;

import com.example.demo.domain.model.OpcaoVoto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para envio de voto do associado")
public class VotoRequestDTO {

    @NotBlank(message = "O CPF do associado é obrigatório.")
    @Schema(description = "CPF do associado votante (com ou sem formatação)", example = "03569852003")
    private String cpf;

    @NotNull(message = "A opção de voto é obrigatória (SIM ou NAO).")
    @Schema(description = "Opção de voto escolhida pelo associado (SIM ou NAO)", example = "SIM")
    private OpcaoVoto voto;
}
