package com.example.demo.service;

import com.example.demo.infrastructure.client.dto.CpfStatus;

public interface CpfValidationService {

    CpfStatus validarCpfParaVotacao(String cpf);
}
