package br.com.estacio.incendio;

import br.com.estacio.incendio.service.SeedData;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private final SeedData seedData;

    public DataLoader(SeedData seedData) {
        this.seedData = seedData;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedData.semear();
    }
}
