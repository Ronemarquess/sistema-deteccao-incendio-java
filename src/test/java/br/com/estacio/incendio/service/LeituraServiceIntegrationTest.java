package br.com.estacio.incendio.service;

import br.com.estacio.incendio.dto.ChamadoDetalhe;
import br.com.estacio.incendio.dto.LeituraRequest;
import br.com.estacio.incendio.dto.LeituraResultado;
import br.com.estacio.incendio.exception.RegraNegocioException;
import br.com.estacio.incendio.model.Bombeiro;
import br.com.estacio.incendio.model.Chamado;
import br.com.estacio.incendio.model.NivelRisco;
import br.com.estacio.incendio.model.Sensor;
import br.com.estacio.incendio.model.StatusChamado;
import br.com.estacio.incendio.repository.BombeiroRepository;
import br.com.estacio.incendio.repository.ChamadoRepository;
import br.com.estacio.incendio.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class LeituraServiceIntegrationTest {

    @Autowired private LeituraService leituraService;
    @Autowired private ChamadoService chamadoService;
    @Autowired private SensorRepository sensorRepository;
    @Autowired private ChamadoRepository chamadoRepository;
    @Autowired private BombeiroRepository bombeiroRepository;

    private final String casarao = "Casarão histórico · Aracaju";

    private Sensor sensor(String nomeLocal, String ponto) {
        return sensorRepository.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getAtivo()))
                .filter(s -> nomeLocal.equals(s.getLocal().getNomeLocal()))
                .filter(s -> ponto.equals(s.getLocalizacao()))
                .findFirst().orElseThrow();
    }

    private Sensor sensorInativo() {
        return sensorRepository.findAll().stream()
                .filter(s -> !Boolean.TRUE.equals(s.getAtivo()))
                .findFirst().orElseThrow();
    }

    @Test
    void leituraDeAlertaCriaChamadoNovo() {
        Sensor sensor = sensor(casarao, "Térreo · sala 2");
        long antes = chamadoRepository.count();

        LeituraResultado resultado = leituraService.registrar(
                new LeituraRequest(sensor.getId(), 85.0, 90.0, 30.0));

        assertEquals(NivelRisco.ALERTA, resultado.nivelRisco());
        assertTrue(resultado.chamadoCriado());
        assertNotNull(resultado.chamadoId());
        assertEquals(antes + 1, chamadoRepository.count());
        Chamado chamado = chamadoRepository.findById(resultado.chamadoId()).orElseThrow();
        assertEquals(StatusChamado.NOVO, chamado.getStatus());
        assertEquals(casarao, chamado.getLocalizacao().getNomeLocal());
        assertTrue(chamado.getElevacaoTemperatura() > 0);
    }

    @Test
    void segundaLeituraNoMesmoLocalAgregaSensor() {
        Sensor primeiro = sensor(casarao, "Térreo · sala 2");
        Sensor segundo = sensor(casarao, "Térreo · corredor");

        LeituraResultado criado = leituraService.registrar(
                new LeituraRequest(primeiro.getId(), 85.0, 90.0, null));
        LeituraResultado agregado = leituraService.registrar(
                new LeituraRequest(segundo.getId(), 90.0, 95.0, null));

        assertEquals(criado.chamadoId(), agregado.chamadoId());
        assertFalse(agregado.chamadoCriado());
        Chamado chamado = chamadoRepository.findById(criado.chamadoId()).orElseThrow();
        assertEquals(2, chamado.getQuantidadeSensores());
        assertEquals(90.0, chamado.getTemperaturaMaxima());
    }

    @Test
    void leituraDeSensorInativoERecusada() {
        Sensor inativo = sensorInativo();
        RegraNegocioException erro = assertThrows(RegraNegocioException.class, () ->
                leituraService.registrar(new LeituraRequest(inativo.getId(), 85.0, 90.0, null)));
        assertTrue(erro.getMessage().contains("Sensor inativo"));
    }

    @Test
    void atencaoENormalNaoCriamChamado() {
        Sensor sensor = sensor(casarao, "Térreo · sala 2");
        long antes = chamadoRepository.count();
        assertEquals(NivelRisco.ATENCAO, leituraService.registrar(
                new LeituraRequest(sensor.getId(), 45.0, 35.0, null)).nivelRisco());
        assertEquals(NivelRisco.NORMAL, leituraService.registrar(
                new LeituraRequest(sensor.getId(), 26.0, 5.0, null)).nivelRisco());
        assertEquals(antes, chamadoRepository.count());
    }

    @Test
    void despacharRegistraNotificacaoSimulada() {
        Sensor sensor = sensor(casarao, "1º andar · arquivo");
        Bombeiro bombeiro = bombeiroRepository.findByEmail("rone@bombeiros.se.gov.br").orElseThrow();
        LeituraResultado resultado = leituraService.registrar(
                new LeituraRequest(sensor.getId(), 86.0, 91.0, null));

        ChamadoDetalhe detalhe = chamadoService.despachar(resultado.chamadoId(), bombeiro.getId());

        assertEquals(StatusChamado.EM_ATENDIMENTO, detalhe.status());
        assertEquals(bombeiro.getNome(), detalhe.bombeiroResponsavel());
        assertEquals(1, detalhe.notificacoes().size());
        assertEquals("PUSH", detalhe.notificacoes().get(0).canal().name());
    }

    @Test
    void transicaoInvalidaNaApiDoServico() {
        Sensor sensor = sensor(casarao, "1º andar · arquivo");
        LeituraResultado resultado = leituraService.registrar(
                new LeituraRequest(sensor.getId(), 86.0, 91.0, null));
        assertThrows(RegraNegocioException.class, () -> chamadoService.encerrar(resultado.chamadoId()));
    }
}
