package com.example.demo.web.controller;

import com.example.demo.facade.PautaFacade;
import com.example.demo.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pautas & Votações", description = "Endpoints principais para gerenciamento de pautas, abertura de sessões, votação e apuração")
public class PautaController {

    private final PautaFacade pautaFacade;

    @PostMapping
    @Operation(summary = "Cadastrar uma nova pauta", description = "Cria uma nova pauta para ser deliberada e votada em assembleia.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pauta cadastrada com sucesso", content = @Content(schema = @Schema(implementation = PautaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PautaResponseDTO> criarPauta(@Valid @RequestBody PautaRequestDTO dto) {
        PautaResponseDTO response = pautaFacade.criarPauta(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as pautas", description = "Retorna a listagem de todas as pautas cadastradas, com seus respectivos status.")
    @ApiResponse(responseCode = "200", description = "Lista de pautas recuperada com sucesso")
    public ResponseEntity<List<PautaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(pautaFacade.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID", description = "Recupera os detalhes de uma pauta específica pelo seu identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pauta encontrada"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PautaResponseDTO> buscarPorId(@Parameter(description = "ID da pauta") @PathVariable Long id) {
        return ResponseEntity.ok(pautaFacade.buscarPorId(id));
    }

    @PostMapping("/{id}/sessao")
    @Operation(summary = "Abrir sessão de votação", description = "Abre a sessão de votação para a pauta indicada. Se o tempo não for informado, adota 1 minuto como padrão.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sessão de votação aberta com sucesso", content = @Content(schema = @Schema(implementation = SessaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Sessão já aberta/existente para a pauta ou parâmetros inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<SessaoResponseDTO> abrirSessao(
            @Parameter(description = "ID da pauta") @PathVariable Long id,
            @Valid @RequestBody(required = false) SessaoRequestDTO dto
    ) {
        SessaoResponseDTO response = pautaFacade.abrirSessao(id, dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(response);
    }

    @GetMapping("/{id}/sessao")
    @Operation(summary = "Consultar sessão de votação", description = "Retorna os dados da sessão de votação da pauta, incluindo horário de início, término e status atual.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sessão encontrada"),
            @ApiResponse(responseCode = "404", description = "Sessão não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<SessaoResponseDTO> buscarSessao(@Parameter(description = "ID da pauta") @PathVariable Long id) {
        return ResponseEntity.ok(pautaFacade.buscarSessao(id));
    }

    @PostMapping("/{id}/votos")
    @Operation(summary = "Registrar voto de associado", description = "Recebe e valida o voto de um associado na pauta (SIM ou NAO), validando tempo de sessão, voto único e elegibilidade do CPF.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso", content = @Content(schema = @Schema(implementation = VotoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "CPF inválido ou requisição malformada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Voto duplicado (associado já votou nesta pauta)", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Sessão de votação fechada ou associado inapto para votar", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<VotoResponseDTO> votar(
            @Parameter(description = "ID da pauta") @PathVariable Long id,
            @Valid @RequestBody VotoRequestDTO dto
    ) {
        VotoResponseDTO response = pautaFacade.votar(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/resultado")
    @Operation(summary = "Contabilizar e obter resultado da votação", description = "Apura os votos da pauta informada e retorna o total, votos Sim, votos Não e o veredito final (Aprovada, Rejeitada, Empate).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado apurado com sucesso", content = @Content(schema = @Schema(implementation = ResultadoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ResultadoResponseDTO> obterResultado(@Parameter(description = "ID da pauta") @PathVariable Long id) {
        ResultadoResponseDTO resultado = pautaFacade.obterResultado(id);
        return ResponseEntity.ok(resultado);
    }
}
