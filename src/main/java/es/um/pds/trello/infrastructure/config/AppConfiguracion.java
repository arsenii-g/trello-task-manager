package es.um.pds.trello.infrastructure.config;

import es.um.pds.trello.domain.services.GeneradorTrazas;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguracion {

    @Bean
    public GeneradorTrazas generadorTrazas() {
        return new GeneradorTrazas();
    }

}