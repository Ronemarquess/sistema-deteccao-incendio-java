package br.com.estacio.incendio;

import br.com.estacio.incendio.config.Limites;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(Limites.class)
public class IncendioApplication {

    public static void main(String[] args) {
        SpringApplication.run(IncendioApplication.class, args);
    }
}
