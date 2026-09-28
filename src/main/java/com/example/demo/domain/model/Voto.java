package com.example.demo.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_voto",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_voto_pauta_associado",
                        columnNames = {"pauta_id", "associado_cpf"}
                )
        },
        indexes = {
                @Index(name = "idx_voto_pauta_id", columnList = "pauta_id"),
                @Index(name = "idx_voto_pauta_opcao", columnList = "pauta_id, opcao_voto")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(name = "associado_cpf", nullable = false, length = 20)
    private String associadoCpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "opcao_voto", nullable = false, length = 10)
    private OpcaoVoto opcaoVoto;

    @Column(name = "data_hora_voto", nullable = false)
    @Builder.Default
    private LocalDateTime dataHoraVoto = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.dataHoraVoto == null) {
            this.dataHoraVoto = LocalDateTime.now();
        }
    }
}
