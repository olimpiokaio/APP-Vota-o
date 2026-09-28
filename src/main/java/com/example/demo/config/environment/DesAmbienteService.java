package com.example.demo.config.environment;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"des", "default"})
public class DesAmbienteService implements AmbienteService {

    @Override
    public String getCodigoAmbiente() {
        return "DES";
    }

    @Override
    public String getDescricao() {
        return "Desenvolvimento Local - Depuração ativa, H2 Console liberado e Swagger habilitado";
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
