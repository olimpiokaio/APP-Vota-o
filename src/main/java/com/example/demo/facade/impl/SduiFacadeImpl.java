package com.example.demo.facade.impl;

import com.example.demo.facade.SduiFacade;
import com.example.demo.service.SduiService;
import com.example.demo.web.dto.sdui.SduiTelaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SduiFacadeImpl implements SduiFacade {

    private final SduiService sduiService;

    @Override
    public SduiTelaDTO obterTelaCadastroPauta() {
        log.info("Facade SDUI: obtendo tela de cadastro de pauta");
        return sduiService.obterTelaCadastroPauta();
    }

    @Override
    public SduiTelaDTO obterTelaSelecaoPautas() {
        log.info("Facade SDUI: obtendo tela de seleção de pautas");
        return sduiService.obterTelaSelecaoPautas();
    }

    @Override
    public SduiTelaDTO obterTelaAberturaSessao(Long id) {
        log.info("Facade SDUI: obtendo tela de abertura de sessão para pauta ID {}", id);
        return sduiService.obterTelaAberturaSessao(id);
    }

    @Override
    public SduiTelaDTO obterTelaVotacao(Long id) {
        log.info("Facade SDUI: obtendo tela de votação para pauta ID {}", id);
        return sduiService.obterTelaVotacao(id);
    }
}
