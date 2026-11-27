package br.com.estacio.incendio.service;

import br.com.estacio.incendio.dto.LeituraDto;
import br.com.estacio.incendio.dto.LeituraRequest;
import br.com.estacio.incendio.dto.LeituraResultado;
import br.com.estacio.incendio.exception.RegraNegocioException;
import br.com.estacio.incendio.exception.RecursoNaoEncontradoException;
import br.com.estacio.incendio.exception.ValidacaoException;
import br.com.estacio.incendio.model.Chamado;
import br.com.estacio.incendio.model.LeituraSensor;
import br.com.estacio.incendio.model.Localizacao;
import br.com.estacio.incendio.model.NivelRisco;
import br.com.estacio.incendio.model.Sensor;
import br.com.estacio.incendio.model.StatusChamado;
import br.com.estacio.incendio.repository.ChamadoRepository;
import br.com.estacio.incendio.repository.LeituraSensorRepository;
import br.com.estacio.incendio.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LeituraService {

    private static final double BASELINE_PADRAO = 25.0;

    private final LeituraSensorRepository leituraRepository;
    private final SensorRepository sensorRepository;
    private final ChamadoRepository chamadoRepository;
    private final AvaliadorRisco avaliadorRisco;

    public LeituraService(LeituraSensorRepository leituraRepository, SensorRepository sensorRepository,
                          ChamadoRepository chamadoRepository, AvaliadorRisco avaliadorRisco) {
        this.leituraRepository = leituraRepository;
        this.sensorRepository = sensorRepository;
        this.chamadoRepository = chamadoRepository;
        this.avaliadorRisco = avaliadorRisco;
    }

    public LeituraResultado registrar(LeituraRequest request) {
        validarFaixas(request);

        Sensor sensor = sensorRepository.findById(request.sensorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Sensor não encontrado: " + request.sensorId()));
        if (!Boolean.TRUE.equals(sensor.getAtivo())) {
            throw new RegraNegocioException("Sensor inativo não gera chamados");
        }

        NivelRisco nivelRisco = avaliadorRisco.avaliar(request.temperatura(), request.nivelFumaca());

        LeituraSensor leitura = new LeituraSensor();
        leitura.setSensor(sensor);
        leitura.setTemperatura(request.temperatura());
        leitura.setNivelFumaca(request.nivelFumaca());
        leitura.setUmidade(request.umidade());
        leitura.setInstanteMedicao(LocalDateTime.now());
        leitura.setNivelRisco(nivelRisco);
        leitura = leituraRepository.save(leitura);

        Long chamadoId = null;
        boolean chamadoCriado = false;
        if (nivelRisco == NivelRisco.ALERTA) {
            ChamadoResultado resultado = obterOuCriarChamado(sensor, leitura);
            chamadoId = resultado.chamado().getId();
            chamadoCriado = resultado.criado();
        }
        return new LeituraResultado(LeituraDto.de(leitura), nivelRisco, chamadoId, chamadoCriado);
    }

    private record ChamadoResultado(Chamado chamado, boolean criado) {
    }

    private ChamadoResultado obterOuCriarChamado(Sensor sensor, LeituraSensor leitura) {
        String nomeLocal = sensor.getLocal() == null ? "" : sensor.getLocal().getNomeLocal();
        List<Chamado> abertos = chamadoRepository
                .findByStatusInAndLocalizacaoNomeLocalOrderByHorarioExatoDesc(
                        List.of(StatusChamado.NOVO, StatusChamado.EM_ATENDIMENTO), nomeLocal);
        if (!abertos.isEmpty()) {
            Chamado chamado = abertos.get(0);
            if (chamado.getSensores().stream().noneMatch(s -> s.getId().equals(sensor.getId()))) {
                chamado.adicionarSensor(sensor);
            }
            atualizarMaximas(chamado, leitura.getTemperatura());
            return new ChamadoResultado(chamado, false);
        }

        Chamado chamado = new Chamado();
        chamado.setStatus(StatusChamado.NOVO);
        chamado.setHorarioExato(leitura.getInstanteMedicao());
        Localizacao local = sensor.getLocal();
        chamado.setLocalizacao(Localizacao.de(
                local == null ? nomeLocal : local.getNomeLocal(),
                local == null ? null : local.getLatitude(),
                local == null ? null : local.getLongitude()));
        chamado.adicionarSensor(sensor);
        double baseline = calcularBaseline(sensor.getId());
        chamado.setTemperaturaMaxima(leitura.getTemperatura());
        chamado.setElevacaoTemperatura(leitura.getTemperatura() - baseline);
        return new ChamadoResultado(chamadoRepository.save(chamado), true);
    }

    private void atualizarMaximas(Chamado chamado, double temperatura) {
        double maximaAtual = chamado.getTemperaturaMaxima() == null
                ? Double.NEGATIVE_INFINITY : chamado.getTemperaturaMaxima();
        if (temperatura > maximaAtual) {
            chamado.setTemperaturaMaxima(temperatura);
        }
        double elevacaoAtual = chamado.getElevacaoTemperatura() == null
                ? Double.NEGATIVE_INFINITY : chamado.getElevacaoTemperatura();
        double elevacao = temperatura - BASELINE_PADRAO;
        if (elevacao > elevacaoAtual) {
            chamado.setElevacaoTemperatura(elevacao);
        }
    }

    private double calcularBaseline(Long sensorId) {
        List<LeituraSensor> normais = leituraRepository
                .findTop5BySensorIdAndNivelRiscoAndInstanteMedicaoBeforeOrderByInstanteMedicaoDesc(
                        sensorId, NivelRisco.NORMAL, LocalDateTime.now());
        if (normais.isEmpty()) {
            return BASELINE_PADRAO;
        }
        return normais.stream().mapToDouble(LeituraSensor::getTemperatura).average().orElse(BASELINE_PADRAO);
    }

    private void validarFaixas(LeituraRequest request) {
        if (request.temperatura() < -40 || request.temperatura() > 150) {
            throw new ValidacaoException("Temperatura fora da faixa permitida (-40 a 150 °C)");
        }
        if (request.nivelFumaca() < 0 || request.nivelFumaca() > 100) {
            throw new ValidacaoException("Nível de fumaça fora da faixa permitida (0 a 100)");
        }
        if (request.umidade() != null && (request.umidade() < 0 || request.umidade() > 100)) {
            throw new ValidacaoException("Umidade fora da faixa permitida (0 a 100)");
        }
    }
}
