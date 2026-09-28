package com.example.demo.web.dto.sdui;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Botão de ação do formulário Server-Driven UI")
public class SduiBotaoDTO {

    @Schema(description = "Texto exibido no botão", example = "Salvar Pauta")
    private String texto;

    @Schema(description = "URL de destino para disparo da requisição", example = "http://localhost:8080/api/v1/pautas")
    private String url;

    @Schema(description = "Método HTTP para envio dos dados", example = "POST")
    private String metodo;

    @Schema(description = "Carga de dados fixa a ser mesclada com os valores dos campos do formulário")
    private Map<String, Object> payload;
}
