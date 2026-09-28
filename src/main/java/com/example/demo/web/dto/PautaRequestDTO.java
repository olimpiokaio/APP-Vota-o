package com.example.demo.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para cadastro de uma nova pauta")
public class PautaRequestDTO {

    @NotBlank(message = "O título da pauta é obrigatório.")
    @Size(min = 3, max = 255, message = "O título da pauta deve ter entre 3 e 255 caracteres.")
    @Schema(description = "Título identificador da pauta", example = "Aprovação do Orçamento Anual 2026")
    private String titulo;

    @Size(max = 2000, message = "A descrição da pauta não pode exceder 2000 caracteres.")
    @Schema(description = "Descrição detalhada dos itens a serem votados", example = "Deliberação sobre os investimentos prioritários para o exercício de 2026.")
    private String descricao;
}
