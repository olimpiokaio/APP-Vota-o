package com.example.demo.web.dto.sdui;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estrutura Server-Driven UI (SDUI) para renderização dinâmica no aplicativo mobile")
public class SduiTelaDTO {

    @Schema(description = "Tipo de tela a ser renderizada", example = "FORMULARIO", allowableValues = {"FORMULARIO", "SELECAO"})
    private String tipo;

    @Schema(description = "Título exibido no topo da tela do aplicativo", example = "Nova Pauta")
    private String titulo;

    @Schema(description = "Mensagem ou subtítulo explicativo", example = "Preencha os dados da pauta para abertura de assembleia.")
    private String mensagem;

    @Schema(description = "Lista de campos para telas do tipo FORMULARIO")
    private List<SduiItemDTO> itens;

    @Schema(description = "Botões de ação da tela do formulário")
    private List<SduiBotaoDTO> botoes;

    @Schema(description = "Lista de itens clicáveis para telas do tipo SELECAO")
    private List<SduiOpcaoDTO> opcoes;

    @Schema(description = "Metadados adicionais para customização da interface")
    private Map<String, Object> metadados;
}
