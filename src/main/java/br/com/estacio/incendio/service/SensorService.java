package br.com.estacio.incendio.service;

import br.com.estacio.incendio.dto.AtivoRequest;
import br.com.estacio.incendio.dto.SensorRequest;
import br.com.estacio.incendio.dto.SensorResumo;
import br.com.estacio.incendio.dto.SensorResponse;
import br.com.estacio.incendio.exception.RecursoNaoEncontradoException;
import br.com.estacio.incendio.model.LeituraSensor;
import br.com.estacio.incendio.model.Localizacao;
import br.com.estacio.incendio.model.Sensor;
import br.com.estacio.incendio.repository.LeituraSensorRepository;
import br.com.estacio.incendio.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SensorService {

    private final SensorRepository repository;
    private final LeituraSensorRepository leituraRepository;
    private final AvaliadorRisco avaliadorRisco;

    public SensorService(SensorRepository repository, LeituraSensorRepository leituraRepository,
                         AvaliadorRisco avaliadorRisco) {
        this.repository = repository;
        this.leituraRepository = leituraRepository;
        this.avaliadorRisco = avaliadorRisco;
    }

    @Transactional(readOnly = true)
    public List<SensorResponse> listar() {
        return repository.findAll().stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public SensorResponse buscar(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public SensorResumo resumo() {
        long todos = repository.count();
        long ativos = repository.findAll().stream()
                .filter(sensor -> Boolean.TRUE.equals(sensor.getAtivo())).count();
        return new SensorResumo(todos, ativos, todos - ativos);
    }

    public SensorResponse criar(SensorRequest request) {
        Sensor sensor = new Sensor();
        aplicarDados(sensor, request);
        sensor.setAtivo(true);
        return paraResponse(repository.save(sensor));
    }

    public SensorResponse atualizar(Long id, SensorRequest request) {
        Sensor sensor = buscarEntidade(id);
        aplicarDados(sensor, request);
        return paraResponse(repository.save(sensor));
    }

    public SensorResponse definirAtivo(Long id, AtivoRequest request) {
        Sensor sensor = buscarEntidade(id);
        sensor.setAtivo(request.ativo());
        return paraResponse(repository.save(sensor));
    }

    @Transactional(readOnly = true)
    public Sensor buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor não encontrado: " + id));
    }

    private void aplicarDados(Sensor sensor, SensorRequest request) {
        sensor.setLocalizacao(request.localizacao());
        sensor.setLocal(Localizacao.de(request.nomeLocal(), request.latitude(), request.longitude()));
    }

    private SensorResponse paraResponse(Sensor sensor) {
        LeituraSensor ultima = leituraRepository
                .findTopBySensorIdOrderByInstanteMedicaoDesc(sensor.getId()).orElse(null);
        String rotuloFumaca = ultima == null ? null : avaliadorRisco.rotuloFumaca(ultima.getNivelFumaca()).name();
        return new SensorResponse(
                sensor.getId(), sensor.getLocalizacao(),
                sensor.getLocal() == null ? null : sensor.getLocal().getNomeLocal(),
                sensor.getLocal() == null ? null : sensor.getLocal().getLatitude(),
                sensor.getLocal() == null ? null : sensor.getLocal().getLongitude(),
                sensor.getAtivo(),
                ultima == null ? null : ultima.getTemperatura(),
                ultima == null ? null : ultima.getNivelFumaca(),
                rotuloFumaca,
                ultima == null ? null : ultima.getInstanteMedicao());
    }
}
