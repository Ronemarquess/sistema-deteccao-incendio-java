package br.com.estacio.incendio.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.estacio.incendio.model.Bombeiro;
import br.com.estacio.incendio.model.LeituraSensor;
import br.com.estacio.incendio.model.Sensor;
import br.com.estacio.incendio.repository.BombeiroRepository;
import br.com.estacio.incendio.repository.LeituraSensorRepository;
import br.com.estacio.incendio.repository.SensorRepository;

@Component
public class DataLoader implements CommandLineRunner {
    private final BombeiroRepository bombeiroRepository;
    private final SensorRepository sensorRepository;
    private final LeituraSensorRepository leituraSensorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(
            BombeiroRepository bombeiroRepository,
            SensorRepository sensorRepository,
            LeituraSensorRepository leituraSensorRepository,
            PasswordEncoder passwordEncoder) {
        this.bombeiroRepository = bombeiroRepository;
        this.sensorRepository = sensorRepository;
        this.leituraSensorRepository = leituraSensorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (bombeiroRepository.count() != 0
                || sensorRepository.count() != 0
                || leituraSensorRepository.count() != 0) {
            return;
        }

        Bombeiro bombeiro = new Bombeiro(
                null,
                "Rone Marques",
                "rone@bombeiros.se.gov.br",
                passwordEncoder.encode("123456"),
                true,
                "BM-2026-01",
                "Tenente",
                "1 BBM - Lagarto",
                "79999999999"
        );
        bombeiroRepository.save(bombeiro);

        Sensor temperatura = sensorRepository.save(
                new Sensor("Temperatura", "1 BBM - Lagarto - Setor Norte"));
        Sensor fumaca = sensorRepository.save(
                new Sensor("Fumaça", "1 BBM - Lagarto - Setor Sul"));
        Sensor umidade = sensorRepository.save(
                new Sensor("Umidade", "1 BBM - Lagarto - Almoxarifado"));

        LocalDateTime agora = LocalDateTime.now();
        leituraSensorRepository.save(new LeituraSensor(
                new BigDecimal("26.4"), agora.minusMinutes(3), temperatura));
        leituraSensorRepository.save(new LeituraSensor(
                new BigDecimal("0.02"), agora.minusMinutes(2), fumaca));
        leituraSensorRepository.save(new LeituraSensor(
                new BigDecimal("58.0"), agora.minusMinutes(1), umidade));
    }
}
