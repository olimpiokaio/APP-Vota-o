package com.example.demo.web.dto.sdui;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Opção selecionável em telas Server-Driven UI")
public class SduiOpcaoDTO {

    @Schema(description = "Identificador da opção", example = "1")
    private String id;

    @Schema(description = "Título principal da opção exibido na interface", example = "Pauta 1: Orçamento 2026")
    private String titulo;

    @Schema(description = "Descrição secundária ou detalhe da opção", example = "Sessão aberta até 23:59")
    private String descricao;

    @Schema(description = "Ação a ser executada ao clicar nesta opção")
    private SduiAcaoDTO acao;
}
