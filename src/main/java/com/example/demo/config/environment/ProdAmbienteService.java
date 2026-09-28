package com.example.demo.config.environment;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("prod")
public class ProdAmbienteService implements AmbienteService {

    @Override
    public String getCodigoAmbiente() {
        return "PROD";
    }

    @Override
    public String getDescricao() {
        return "Produção - Alta disponibilidade, máxima segurança, logs estritos e auditoria";
    }

    @Override
    public boolean isProducao() {
        return true;
    }

    @Override
    public boolean isDocumentacaoHabilitada() {
        return false;
    }
}
