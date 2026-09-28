package com.example.demo.config.environment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("tes")
public class TesProfileConfig {

    @Bean
    public InfraestruturaAmbiente infraestruturaAmbiente() {
        return new InfraestruturaAmbiente(
                "TES",
                "H2 Database Dedicado a Homologação (votedb_tes)",
                "TEST_STAGING",
                false
        );
    }
}
