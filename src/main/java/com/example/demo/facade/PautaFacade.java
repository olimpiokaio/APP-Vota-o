package com.example.demo.facade;

import com.example.demo.web.dto.*;

import java.util.List;

public interface PautaFacade {

    PautaResponseDTO criarPauta(PautaRequestDTO dto);

    List<PautaResponseDTO> listarTodas();

    PautaResponseDTO buscarPorId(Long id);

    SessaoResponseDTO abrirSessao(Long id, SessaoRequestDTO dto);

    SessaoResponseDTO buscarSessao(Long id);

    VotoResponseDTO votar(Long id, VotoRequestDTO dto);

    ResultadoResponseDTO obterResultado(Long id);
}
