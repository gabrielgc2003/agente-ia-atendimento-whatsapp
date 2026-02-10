package ggctech.whatsappai.config.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EvolutionApiConfig {

    @Bean
    public RestTemplate evolutionRestTemplate() {
        return new RestTemplate();
    }
}

