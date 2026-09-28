package com.example.demo.infrastructure.client;

import com.example.demo.exception.AssociadoInaptoException;
import com.example.demo.exception.CpfInvalidoException;
import com.example.demo.infrastructure.client.dto.CpfStatus;
import com.example.demo.infrastructure.util.CpfUtil;
import com.example.demo.service.CpfValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class CpfValidationServiceImpl implements CpfValidationService {

    private final Random random;

    public CpfValidationServiceImpl() {
        this(new Random());
    }

    public CpfValidationServiceImpl(Random random) {
        this.random = random;
    }

    @Override
    public CpfStatus validarCpfParaVotacao(String cpf) {
        String cpfLimpo = CpfUtil.limpar(cpf);

        if (!CpfUtil.isValido(cpfLimpo)) {
            log.warn("Tentativa de voto com CPF matematicamente inválido: {}", cpf);
            throw new CpfInvalidoException("CPF informado é inválido: formato ou dígitos verificadores incorretos.");
        }

        log.info("Simulando chamada para API externa de validação de CPF para: {}", cpfLimpo);

        // Simulação randômica: 80% de chance de estar apto (ABLE_TO_VOTE), 20% inapto (UNABLE_TO_VOTE)
        int sorteio = random.nextInt(100); // 0 a 99
        if (sorteio < 80) {
            log.info("Simulação de API externa: CPF {} APTO para votar (ABLE_TO_VOTE).", cpfLimpo);
            return CpfStatus.ABLE_TO_VOTE;
        } else {
            log.info("Simulação de API externa: CPF {} INAPTO para votar (UNABLE_TO_VOTE).", cpfLimpo);
            throw new AssociadoInaptoException("Associado com CPF " + cpfLimpo + " não está habilitado para votar nesta pauta.");
        }
    }
}
