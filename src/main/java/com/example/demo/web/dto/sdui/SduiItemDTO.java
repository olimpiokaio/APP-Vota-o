package com.example.demo.web.dto.sdui;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Campo de entrada de dados para telas Server-Driven UI")
public class SduiItemDTO {

    @Schema(description = "Identificador da chave do campo no payload JSON enviado", example = "titulo")
    private String nome;

    @Schema(description = "Texto do rótulo exibido ao usuário", example = "Título da Pauta")
    private String label;

    @Schema(description = "Tipo de entrada do campo", example = "TEXTO", allowableValues = {"TEXTO", "NUMERICO", "SELECAO"})
    private String tipo;

    @Schema(description = "Indica se o preenchimento do campo é mandatório", example = "true")
    private boolean obrigatorio;

    @Schema(description = "Valor inicial ou sugerido para o campo", example = "1")
    private Object valorPadrao;

    @Schema(description = "Lista de opções disponíveis quando o tipo de campo for SELECAO")
    private List<String> opcoes;
}
