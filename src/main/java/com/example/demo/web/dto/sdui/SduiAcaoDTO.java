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
@Schema(description = "Ação executada ao selecionar uma opção no Server-Driven UI")
public class SduiAcaoDTO {

    @Schema(description = "URL de destino para disparo da requisição", example = "http://localhost:8080/api/v1/pautas/1/votos")
    private String url;

    @Schema(description = "Método HTTP utilizado para a ação", example = "POST")
    private String metodo;

    @Schema(description = "Corpo de dados a ser enviado na requisição")
    private Map<String, Object> payload;
}
