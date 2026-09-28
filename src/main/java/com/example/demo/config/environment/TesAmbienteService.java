package com.example.demo.config.environment;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("tes")
public class TesAmbienteService implements AmbienteService {

    @Override
    public String getCodigoAmbiente() {
        return "TES";
    }

    @Override
    public String getDescricao() {
        return "Homologação e Testes de Integração - Ambiente estável para validações e QA";
    }

    @Override
    public boolean isProducao() {
        return false;
    }

    @Override
    public boolean isDocumentacaoHabilitada() {
        return true;
    }
}
