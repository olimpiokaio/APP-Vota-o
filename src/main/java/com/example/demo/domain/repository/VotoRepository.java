package com.example.demo.domain.repository;

import com.example.demo.domain.model.OpcaoVoto;
import com.example.demo.domain.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByPautaIdAndAssociadoCpf(Long pautaId, String associadoCpf);

    long countByPautaId(Long pautaId);

    long countByPautaIdAndOpcaoVoto(Long pautaId, OpcaoVoto opcaoVoto);

    List<Voto> findByPautaId(Long pautaId);
}
