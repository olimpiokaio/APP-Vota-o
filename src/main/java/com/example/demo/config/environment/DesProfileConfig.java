package com.example.demo.config.environment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile({"des", "default"})
public class DesProfileConfig {

    @Bean
    public InfraestruturaAmbiente infraestruturaAmbiente() {
        return new InfraestruturaAmbiente(
                "DES",
                "H2 Database em Memória (votedb_des)",
                "DEV_PERMISSIVE",
                true
        );
    }
}
