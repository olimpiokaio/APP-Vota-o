package com.example.demo.facade.impl;

import com.example.demo.domain.model.Pauta;
import com.example.demo.domain.model.SessaoVotacao;
import com.example.demo.facade.PautaFacade;
import com.example.demo.service.PautaService;
import com.example.demo.service.SessaoVotacaoService;
import com.example.demo.service.VotacaoService;
import com.example.demo.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PautaFacadeImpl implements PautaFacade {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final VotacaoService votacaoService;

    @Override
    public PautaResponseDTO criarPauta(PautaRequestDTO dto) {
        log.info("Facade: criando pauta com título '{}'", dto.getTitulo());
        Pauta criada = pautaService.criarPauta(dto);
        return pautaService.toResponseDTO(criada);
    }

    @Override
    public List<PautaResponseDTO> listarTodas() {
        log.info("Facade: listando todas as pautas");
        return pautaService.listarTodas().stream()
                .map(pautaService::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PautaResponseDTO buscarPorId(Long id) {
        log.info("Facade: buscando pauta ID {}", id);
        Pauta pauta = pautaService.buscarPorId(id);
        return pautaService.toResponseDTO(pauta);
    }

    @Override
    public SessaoResponseDTO abrirSessao(Long id, SessaoRequestDTO dto) {
        log.info("Facade: abrindo sessão para pauta ID {}", id);
        SessaoVotacao sessao = sessaoVotacaoService.abrirSessao(id, dto);
        return sessaoVotacaoService.toResponseDTO(sessao);
    }

    @Override
    public SessaoResponseDTO buscarSessao(Long id) {
        log.info("Facade: buscando sessão da pauta ID {}", id);
        SessaoVotacao sessao = sessaoVotacaoService.buscarPorPautaId(id);
        return sessaoVotacaoService.toResponseDTO(sessao);
    }

    @Override
    public VotoResponseDTO votar(Long id, VotoRequestDTO dto) {
        log.info("Facade: registrando voto para pauta ID {}", id);
        return votacaoService.registrarVoto(id, dto);
    }

    @Override
    public ResultadoResponseDTO obterResultado(Long id) {
        log.info("Facade: obtendo resultado da pauta ID {}", id);
        return votacaoService.apurarResultado(id);
    }
}
