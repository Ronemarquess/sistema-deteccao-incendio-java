package br.com.estacio.incendio.service;

import br.com.estacio.incendio.exception.RegraNegocioException;
import br.com.estacio.incendio.repository.ChamadoRepository;
import br.com.estacio.incendio.repository.LeituraSensorRepository;
import br.com.estacio.incendio.repository.NotificacaoRepository;
import br.com.estacio.incendio.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SimulacaoService {

    private final ChamadoRepository chamadoRepository;
    private final LeituraSensorRepository leituraRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final SensorRepository sensorRepository;
    private final SeedData seedData;
    private final boolean habilitada;

    public SimulacaoService(ChamadoRepository chamadoRepository, LeituraSensorRepository leituraRepository,
                            NotificacaoRepository notificacaoRepository, SensorRepository sensorRepository,
                            SeedData seedData,
                            @Value("${safealert.simulacao.habilitada:true}") boolean habilitada) {
        this.chamadoRepository = chamadoRepository;
        this.leituraRepository = leituraRepository;
        this.notificacaoRepository = notificacaoRepository;
        this.sensorRepository = sensorRepository;
        this.seedData = seedData;
        this.habilitada = habilitada;
    }

    @Transactional
    public void reset() {
        if (!habilitada) {
            throw new RegraNegocioException("Reset da simulação desabilitado por configuração");
        }
        notificacaoRepository.deleteAll();
        chamadoRepository.deleteAll();
        leituraRepository.deleteAll();
        sensorRepository.findAll().forEach(seedData::criarLeiturasNormais);
    }
}
