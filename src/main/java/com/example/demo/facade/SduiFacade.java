package com.example.demo.facade;

import com.example.demo.web.dto.sdui.SduiTelaDTO;

public interface SduiFacade {

    SduiTelaDTO obterTelaCadastroPauta();

    SduiTelaDTO obterTelaSelecaoPautas();

    SduiTelaDTO obterTelaAberturaSessao(Long id);

    SduiTelaDTO obterTelaVotacao(Long id);
}
