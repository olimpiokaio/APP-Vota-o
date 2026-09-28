package com.example.demo.web.controller;

import com.example.demo.facade.SduiFacade;
import com.example.demo.web.dto.sdui.SduiTelaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sdui")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Server-Driven UI (AP)", description = "Endpoints para fornecimento de contratos visuais e fluxos dinâmicos para aplicativos mobile")
public class SduiController {

    private final SduiFacade sduiFacade;

    @GetMapping("/telas/pauta-form")
    @Operation(summary = "Obter tela FORMULARIO para cadastro de pauta", description = "Retorna o contrato SDUI de formulário com campos e botão de ação para criação de pauta.")
    @ApiResponse(responseCode = "200", description = "Estrutura da tela retornada com sucesso", content = @Content(schema = @Schema(implementation = SduiTelaDTO.class)))
    public ResponseEntity<SduiTelaDTO> telaCadastroPauta() {
        return ResponseEntity.ok(sduiFacade.obterTelaCadastroPauta());
    }

    @GetMapping("/telas/pautas")
    @Operation(summary = "Obter tela SELECAO para listagem de pautas", description = "Retorna o contrato SDUI do tipo SELECAO com opções de pautas disponíveis e ações de navegação.")
    @ApiResponse(responseCode = "200", description = "Estrutura da tela retornada com sucesso", content = @Content(schema = @Schema(implementation = SduiTelaDTO.class)))
    public ResponseEntity<SduiTelaDTO> telaSelecaoPautas() {
        return ResponseEntity.ok(sduiFacade.obterTelaSelecaoPautas());
    }

    @GetMapping("/telas/pautas/{id}/sessao-form")
    @Operation(summary = "Obter tela FORMULARIO para abertura de sessão", description = "Retorna o contrato SDUI com campo de duração e botão para abertura da sessão de votação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estrutura da tela retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<SduiTelaDTO> telaAberturaSessao(@Parameter(description = "ID da pauta") @PathVariable Long id) {
        return ResponseEntity.ok(sduiFacade.obterTelaAberturaSessao(id));
    }

    @GetMapping("/telas/pautas/{id}/votar")
    @Operation(summary = "Obter tela FORMULARIO para registrar voto", description = "Retorna o contrato SDUI com campos de CPF, seleção Sim/Não e botão para submeter o voto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estrutura da tela retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<SduiTelaDTO> telaVotacao(@Parameter(description = "ID da pauta") @PathVariable Long id) {
        return ResponseEntity.ok(sduiFacade.obterTelaVotacao(id));
    }
}
