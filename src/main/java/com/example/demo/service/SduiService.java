package com.example.demo.service;

import com.example.demo.domain.model.Pauta;
import com.example.demo.domain.model.StatusSessao;
import com.example.demo.web.dto.sdui.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SduiService {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public SduiTelaDTO obterTelaCadastroPauta() {
        return SduiTelaDTO.builder()
                .tipo("FORMULARIO")
                .titulo("Cadastrar Nova Pauta")
                .mensagem("Preencha os dados abaixo para cadastrar uma nova pauta para deliberação.")
                .itens(List.of(
                        SduiItemDTO.builder()
                                .nome("titulo")
                                .label("Título da Pauta")
                                .tipo("TEXTO")
                                .obrigatorio(true)
                                .build(),
                        SduiItemDTO.builder()
                                .nome("descricao")
                                .label("Descrição dos Itens a Votar")
                                .tipo("TEXTO")
                                .obrigatorio(false)
                                .build()
                ))
                .botoes(List.of(
                        SduiBotaoDTO.builder()
                                .texto("Salvar Pauta")
                                .url(baseUrl + "/api/v1/pautas")
                                .metodo("POST")
                                .build()
                ))
                .build();
    }

    public SduiTelaDTO obterTelaAberturaSessao(Long pautaId) {
        Pauta pauta = pautaService.buscarPorId(pautaId);

        return SduiTelaDTO.builder()
                .tipo("FORMULARIO")
                .titulo("Abrir Sessão de Votação")
                .mensagem("Pauta: " + pauta.getTitulo())
                .itens(List.of(
                        SduiItemDTO.builder()
                                .nome("duracaoMinutos")
                                .label("Duração da Sessão (em minutos)")
                                .tipo("NUMERICO")
                                .obrigatorio(false)
                                .valorPadrao(1)
                                .build()
                ))
                .botoes(List.of(
                        SduiBotaoDTO.builder()
                                .texto("Iniciar Votação")
                                .url(baseUrl + "/api/v1/pautas/" + pautaId + "/sessao")
                                .metodo("POST")
                                .build()
                ))
                .build();
    }

    public SduiTelaDTO obterTelaVotacao(Long pautaId) {
        Pauta pauta = pautaService.buscarPorId(pautaId);

        return SduiTelaDTO.builder()
                .tipo("FORMULARIO")
                .titulo("Votar na Pauta")
                .mensagem("Pauta: " + pauta.getTitulo())
                .itens(List.of(
                        SduiItemDTO.builder()
                                .nome("cpf")
                                .label("CPF do Associado")
                                .tipo("TEXTO")
                                .obrigatorio(true)
                                .build(),
                        SduiItemDTO.builder()
                                .nome("voto")
                                .label("Opção de Voto")
                                .tipo("SELECAO")
                                .obrigatorio(true)
                                .opcoes(List.of("SIM", "NAO"))
                                .build()
                ))
                .botoes(List.of(
                        SduiBotaoDTO.builder()
                                .texto("Confirmar Voto")
                                .url(baseUrl + "/api/v1/pautas/" + pautaId + "/votos")
                                .metodo("POST")
                                .build()
                ))
                .build();
    }

    public SduiTelaDTO obterTelaSelecaoPautas() {
        List<Pauta> pautas = pautaService.listarTodas();

        List<SduiOpcaoDTO> opcoes = pautas.stream().map(pauta -> {
            StatusSessao status = sessaoVotacaoService.obterStatusSessao(pauta);
            return SduiOpcaoDTO.builder()
                    .id(pauta.getId().toString())
                    .titulo(pauta.getTitulo())
                    .descricao("Status: " + status.getDescricao())
                    .acao(SduiAcaoDTO.builder()
                            .url(baseUrl + "/api/v1/pautas/" + pauta.getId() + "/resultado")
                            .metodo("GET")
                            .build())
                    .build();
        }).collect(Collectors.toList());

        return SduiTelaDTO.builder()
                .tipo("SELECAO")
                .titulo("Pautas Disponíveis")
                .mensagem("Selecione uma pauta para verificar detalhes e resultado da apuração.")
                .opcoes(opcoes)
                .build();
    }
}
