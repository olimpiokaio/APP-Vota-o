package com.example.demo.service;

import com.example.demo.domain.model.Pauta;
import com.example.demo.domain.model.StatusSessao;
import com.example.demo.domain.repository.PautaRepository;
import com.example.demo.exception.EntidadeNaoEncontradaException;
import com.example.demo.web.dto.PautaRequestDTO;
import com.example.demo.web.dto.PautaResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PautaService {

    private final PautaRepository pautaRepository;

    @Transactional
    public Pauta criarPauta(PautaRequestDTO dto) {
        log.info("Cadastrando nova pauta com título: {}", dto.getTitulo());

        Pauta pauta = Pauta.builder()
                .titulo(dto.getTitulo().trim())
                .descricao(dto.getDescricao() != null ? dto.getDescricao().trim() : null)
                .build();

        Pauta salva = pautaRepository.save(pauta);
        log.info("Pauta cadastrada com sucesso com ID: {}", salva.getId());
        return salva;
    }

    @Transactional(readOnly = true)
    public Pauta buscarPorId(Long id) {
        return pautaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pauta com ID " + id + " não foi encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Pauta> listarTodas() {
        return pautaRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public PautaResponseDTO toResponseDTO(Pauta pauta) {
        StatusSessao status = StatusSessao.NAO_INICIADA;
        if (pauta.getSessao() != null) {
            status = pauta.getSessao().getStatus();
        }

        return PautaResponseDTO.builder()
                .id(pauta.getId())
                .titulo(pauta.getTitulo())
                .descricao(pauta.getDescricao())
                .dataCriacao(pauta.getDataCriacao())
                .statusSessao(status)
                .build();
    }
}
