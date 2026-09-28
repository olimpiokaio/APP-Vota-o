package com.example.demo.service;

import com.example.demo.domain.model.Pauta;
import com.example.demo.domain.model.SessaoVotacao;
import com.example.demo.domain.model.StatusSessao;
import com.example.demo.domain.repository.SessaoVotacaoRepository;
import com.example.demo.exception.EntidadeNaoEncontradaException;
import com.example.demo.exception.RegraDeNegocioException;
import com.example.demo.web.dto.SessaoRequestDTO;
import com.example.demo.web.dto.SessaoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessaoVotacaoService {

    private static final int DURACAO_PADRAO_MINUTOS = 1;

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaService pautaService;

    @Transactional
    public SessaoVotacao abrirSessao(Long pautaId, SessaoRequestDTO dto) {
        log.info("Tentativa de abertura de sessão de votação para a pauta ID: {}", pautaId);

        Pauta pauta = pautaService.buscarPorId(pautaId);

        if (sessaoVotacaoRepository.existsByPautaId(pautaId)) {
            log.warn("Sessão já existente para a pauta ID: {}", pautaId);
            throw new RegraDeNegocioException("Não é permitido abrir múltiplas sessões ou reabrir sessão para a pauta ID " + pautaId + ".");
        }

        LocalDateTime dataHoraInicio = LocalDateTime.now();
        LocalDateTime dataHoraFim;

        if (dto != null && dto.getDuracaoSegundos() != null && dto.getDuracaoSegundos() > 0) {
            dataHoraFim = dataHoraInicio.plusSeconds(dto.getDuracaoSegundos());
        } else if (dto != null && dto.getDuracaoMinutos() != null && dto.getDuracaoMinutos() > 0) {
            dataHoraFim = dataHoraInicio.plusMinutes(dto.getDuracaoMinutos());
        } else {
            dataHoraFim = dataHoraInicio.plusMinutes(DURACAO_PADRAO_MINUTOS);
        }

        SessaoVotacao sessao = SessaoVotacao.builder()
                .pauta(pauta)
                .dataHoraInicio(dataHoraInicio)
                .dataHoraFim(dataHoraFim)
                .build();

        SessaoVotacao salva = sessaoVotacaoRepository.save(sessao);
        pauta.setSessao(salva);

        log.info("Sessão ID {} aberta com sucesso para a pauta ID {}. Válida até {}", salva.getId(), pautaId, dataHoraFim);
        return salva;
    }

    @Transactional(readOnly = true)
    public SessaoVotacao buscarPorPautaId(Long pautaId) {
        return sessaoVotacaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Nenhuma sessão de votação encontrada para a pauta ID " + pautaId + "."));
    }

    @Transactional(readOnly = true)
    public StatusSessao obterStatusSessao(Pauta pauta) {
        if (pauta == null) {
            return StatusSessao.NAO_INICIADA;
        }
        return sessaoVotacaoRepository.findByPautaId(pauta.getId())
                .map(SessaoVotacao::getStatus)
                .orElse(StatusSessao.NAO_INICIADA);
    }

    public SessaoResponseDTO toResponseDTO(SessaoVotacao sessao) {
        LocalDateTime agora = LocalDateTime.now();
        return SessaoResponseDTO.builder()
                .id(sessao.getId())
                .pautaId(sessao.getPauta() != null ? sessao.getPauta().getId() : null)
                .dataHoraInicio(sessao.getDataHoraInicio())
                .dataHoraFim(sessao.getDataHoraFim())
                .aberta(sessao.isAberta(agora))
                .status(sessao.getStatus(agora))
                .build();
    }
}
