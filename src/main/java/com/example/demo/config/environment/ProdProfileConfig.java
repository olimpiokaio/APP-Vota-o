package com.example.demo.config.environment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("prod")
public class ProdProfileConfig {

    @Bean
    public InfraestruturaAmbiente infraestruturaAmbiente() {
        return new InfraestruturaAmbiente(
                "PROD",
                "Banco de Dados Corporativo de Produção",
                "PROD_STRICT",
                false
        );
    }
}
