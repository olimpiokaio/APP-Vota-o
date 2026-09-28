package com.example.demo.service;

import com.example.demo.domain.model.*;
import com.example.demo.domain.repository.VotoRepository;
import com.example.demo.exception.SessaoNaoAbertaException;
import com.example.demo.exception.VotoDuplicadoException;
import com.example.demo.infrastructure.util.CpfUtil;
import com.example.demo.web.dto.ResultadoResponseDTO;
import com.example.demo.web.dto.VotoRequestDTO;
import com.example.demo.web.dto.VotoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VotacaoService {

    private final VotoRepository votoRepository;
    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final CpfValidationService cpfValidationService;

    @Transactional
    public VotoResponseDTO registrarVoto(Long pautaId, VotoRequestDTO dto) {
        log.info("Processando tentativa de voto na pauta ID: {}", pautaId);

        Pauta pauta = pautaService.buscarPorId(pautaId);

        SessaoVotacao sessao;
        try {
            sessao = sessaoVotacaoService.buscarPorPautaId(pautaId);
        } catch (Exception e) {
            throw new SessaoNaoAbertaException("Nenhuma sessão de votação foi aberta para a pauta ID " + pautaId + ".");
        }

        if (!sessao.isAberta()) {
            log.warn("Tentativa de voto rejeitada: sessão da pauta ID {} não está aberta (Status: {})", pautaId, sessao.getStatus());
            throw new SessaoNaoAbertaException("A sessão de votação para esta pauta está " + sessao.getStatus().name().toLowerCase() + ".");
        }

        String cpfLimpo = CpfUtil.limpar(dto.getCpf());

        cpfValidationService.validarCpfParaVotacao(cpfLimpo);

        if (votoRepository.existsByPautaIdAndAssociadoCpf(pautaId, cpfLimpo)) {
            log.warn("Tentativa de voto duplicado para associado com CPF {} na pauta ID {}", cpfLimpo, pautaId);
            throw new VotoDuplicadoException("O associado com CPF " + cpfLimpo + " já votou na pauta ID " + pautaId + ".");
        }

        Voto voto = Voto.builder()
                .pauta(pauta)
                .associadoCpf(cpfLimpo)
                .opcaoVoto(dto.getVoto())
                .build();

        try {
            Voto salvo = votoRepository.saveAndFlush(voto);
            log.info("Voto registrado com sucesso! ID: {}, Pauta: {}, Opção: {}", salvo.getId(), pautaId, salvo.getOpcaoVoto());

            return VotoResponseDTO.builder()
                    .id(salvo.getId())
                    .pautaId(pautaId)
                    .associadoCpf(salvo.getAssociadoCpf())
                    .voto(salvo.getOpcaoVoto())
                    .dataHoraVoto(salvo.getDataHoraVoto())
                    .mensagem("Voto registrado com sucesso.")
                    .build();

        } catch (DataIntegrityViolationException e) {
            log.warn("Violação de chave única ao salvar voto concorrente para associado {} na pauta {}", cpfLimpo, pautaId);
            throw new VotoDuplicadoException("O associado com CPF " + cpfLimpo + " já votou na pauta ID " + pautaId + ".");
        }
    }

    @Transactional(readOnly = true)
    public ResultadoResponseDTO apurarResultado(Long pautaId) {
        log.info("Apurando resultado dos votos para a pauta ID: {}", pautaId);

        Pauta pauta = pautaService.buscarPorId(pautaId);
        StatusSessao statusSessao = sessaoVotacaoService.obterStatusSessao(pauta);

        long totalVotos = votoRepository.countByPautaId(pautaId);
        long votosSim = votoRepository.countByPautaIdAndOpcaoVoto(pautaId, OpcaoVoto.SIM);
        long votosNao = votoRepository.countByPautaIdAndOpcaoVoto(pautaId, OpcaoVoto.NAO);

        ResultadoVotacao resultado;
        if (totalVotos == 0) {
            resultado = ResultadoVotacao.SEM_VOTOS;
        } else if (votosSim > votosNao) {
            resultado = ResultadoVotacao.APROVADA;
        } else if (votosNao > votosSim) {
            resultado = ResultadoVotacao.REJEITADA;
        } else {
            resultado = ResultadoVotacao.EMPATE;
        }

        log.info("Resultado apurado para pauta ID {}: Total={}, Sim={}, Não={}, Resultado={}",
                pautaId, totalVotos, votosSim, votosNao, resultado);

        return ResultadoResponseDTO.builder()
                .pautaId(pautaId)
                .tituloPauta(pauta.getTitulo())
                .descricaoPauta(pauta.getDescricao())
                .statusSessao(statusSessao)
                .totalVotos(totalVotos)
                .votosSim(votosSim)
                .votosNao(votosNao)
                .resultado(resultado)
                .descricaoResultado(resultado.getDescricao())
                .build();
    }
}
