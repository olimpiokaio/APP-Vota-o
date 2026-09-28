package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_sessao_votacao",
        indexes = {
                @Index(name = "idx_sessao_data_fim", columnList = "data_hora_fim")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pauta_id", unique = true, nullable = false)
    private Pauta pauta;

    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim", nullable = false)
    private LocalDateTime dataHoraFim;

    public boolean isAberta(LocalDateTime momento) {
        if (momento == null || dataHoraInicio == null || dataHoraFim == null) {
            return false;
        }
        return !momento.isBefore(dataHoraInicio) && momento.isBefore(dataHoraFim);
    }

    public boolean isAberta() {
        return isAberta(LocalDateTime.now());
    }

    public StatusSessao getStatus(LocalDateTime momento) {
        if (dataHoraInicio == null || dataHoraFim == null) {
            return StatusSessao.NAO_INICIADA;
        }
        if (momento.isBefore(dataHoraInicio)) {
            return StatusSessao.NAO_INICIADA;
        }
        if (isAberta(momento)) {
            return StatusSessao.ABERTA;
        }
        return StatusSessao.ENCERRADA;
    }

    public StatusSessao getStatus() {
        return getStatus(LocalDateTime.now());
    }
}
