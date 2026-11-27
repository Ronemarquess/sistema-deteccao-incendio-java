package br.com.estacio.incendio.service;

import br.com.estacio.incendio.model.Bombeiro;
import br.com.estacio.incendio.model.Chamado;
import br.com.estacio.incendio.model.LeituraSensor;
import br.com.estacio.incendio.model.Localizacao;
import br.com.estacio.incendio.model.NivelRisco;
import br.com.estacio.incendio.model.Sensor;
import br.com.estacio.incendio.model.StatusChamado;
import br.com.estacio.incendio.repository.BombeiroRepository;
import br.com.estacio.incendio.repository.ChamadoRepository;
import br.com.estacio.incendio.repository.LeituraSensorRepository;
import br.com.estacio.incendio.repository.SensorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeedData {

    private record LocalDemo(String nome, Double latitude, Double longitude,
                             List<String> pontos, boolean ativo) {
    }

    private static final List<LocalDemo> LOCAIS = List.of(
            new LocalDemo("Casarão histórico · Aracaju", -10.9111, -37.0717,
                    List.of("Térreo · sala 2", "Térreo · corredor", "1º andar · arquivo"), true),
            new LocalDemo("Galpão logístico · Barra dos Coqueiros", -10.9060, -37.0380,
                    List.of("Setor A", "Setor B"), true),
            new LocalDemo("Data center · Centro", -10.9140, -37.0500,
                    List.of("Rack 3"), true),
            new LocalDemo("Mercado antigo · São Cristóvão", -11.0145, -37.2055,
                    List.of("Box 7"), false)
    );

    private final SensorRepository sensorRepository;
    private final LeituraSensorRepository leituraRepository;
    private final ChamadoRepository chamadoRepository;
    private final BombeiroRepository bombeiroRepository;
    private final PasswordEncoder passwordEncoder;
    private final AvaliadorRisco avaliadorRisco;

    public SeedData(SensorRepository sensorRepository, LeituraSensorRepository leituraRepository,
                    ChamadoRepository chamadoRepository, BombeiroRepository bombeiroRepository,
                    PasswordEncoder passwordEncoder, AvaliadorRisco avaliadorRisco) {
        this.sensorRepository = sensorRepository;
        this.leituraRepository = leituraRepository;
        this.chamadoRepository = chamadoRepository;
        this.bombeiroRepository = bombeiroRepository;
        this.passwordEncoder = passwordEncoder;
        this.avaliadorRisco = avaliadorRisco;
    }

    @Transactional
    public void semear() {
        if (sensorRepository.count() > 0) {
            return;
        }
        Bombeiro bombeiro = semearBombeiro();
        List<Sensor> sensores = semearSensores();
        for (Sensor sensor : sensores) {
            criarLeiturasNormais(sensor);
        }
        semearHistorico(sensores, bombeiro);
    }

    @Transactional
    public void criarLeiturasNormais(Sensor sensor) {
        LocalDateTime agora = LocalDateTime.now();
        for (int i = 0; i < 3; i++) {
            double temperatura = 25.5 + (i * 0.3);
            double fumaca = 4.0 + i;
            LeituraSensor leitura = new LeituraSensor();
            leitura.setSensor(sensor);
            leitura.setTemperatura(temperatura);
            leitura.setNivelFumaca(fumaca);
            leitura.setUmidade(55.0);
            leitura.setInstanteMedicao(agora.minusMinutes(2L * (i + 1)));
            leitura.setNivelRisco(avaliadorRisco.avaliar(temperatura, fumaca));
            leituraRepository.save(leitura);
        }
    }

    private Bombeiro semearBombeiro() {
        if (bombeiroRepository.existsByEmail("rone@bombeiros.se.gov.br")) {
            return bombeiroRepository.findByEmail("rone@bombeiros.se.gov.br").orElseThrow();
        }
        Bombeiro bombeiro = new Bombeiro();
        bombeiro.setNome("Rone Marques");
        bombeiro.setEmail("rone@bombeiros.se.gov.br");
        bombeiro.setSenha(passwordEncoder.encode("123456"));
        bombeiro.setMatricula("12345");
        bombeiro.setPatente("Cabo");
        bombeiro.setBatalhao("1º BBM");
        bombeiro.setTelefone("(79) 99999-0000");
        bombeiro.setAtivo(true);
        return bombeiroRepository.save(bombeiro);
    }

    private List<Sensor> semearSensores() {
        List<Sensor> criados = new java.util.ArrayList<>();
        for (LocalDemo local : LOCAIS) {
            for (String ponto : local.pontos()) {
                if (sensorRepository.existsByLocalizacaoAndLocalNomeLocal(ponto, local.nome())) {
                    continue;
                }
                Sensor sensor = new Sensor();
                sensor.setLocalizacao(ponto);
                sensor.setLocal(Localizacao.de(local.nome(), local.latitude(), local.longitude()));
                sensor.setAtivo(local.ativo());
                criados.add(sensorRepository.save(sensor));
            }
        }
        return criados;
    }

    private void semearHistorico(List<Sensor> sensores, Bombeiro bombeiro) {
        if (chamadoRepository.count() > 0 || sensores.isEmpty()) {
            return;
        }
        LocalDateTime agora = LocalDateTime.now();

        Chamado encerrado = novoChamado(
                StatusChamado.ENCERRADO, sensores.get(0), 62.0, 36.0,
                agora.minusHours(3), agora.minusHours(2), bombeiro);
        chamadoRepository.save(encerrado);

        Chamado falsoAlarme = novoChamado(
                StatusChamado.FALSO_ALARME, sensores.get(1), 41.0, 15.0,
                agora.minusDays(1).minusHours(2), agora.minusDays(1).minusHours(1), bombeiro);
        chamadoRepository.save(falsoAlarme);
    }

    private Chamado novoChamado(StatusChamado status, Sensor sensor, double temperatura,
                                double elevacao, LocalDateTime horario, LocalDateTime encerrado,
                                Bombeiro bombeiro) {
        Chamado chamado = new Chamado();
        chamado.setStatus(StatusChamado.NOVO);
        chamado.setHorarioExato(horario);
        chamado.setEncerradoEm(encerrado);
        chamado.setTemperaturaMaxima(temperatura);
        chamado.setElevacaoTemperatura(elevacao);
        chamado.setLocalizacao(Localizacao.de(
                sensor.getLocal().getNomeLocal(),
                sensor.getLocal().getLatitude(),
                sensor.getLocal().getLongitude()));
        chamado.adicionarSensor(sensor);
        chamado.setBombeiroResponsavel(bombeiro);
        chamado.setStatus(status);
        return chamado;
    }
}
