package br.com.estacio.incendio.service;

import br.com.estacio.incendio.dto.ChamadoDetalhe;
import br.com.estacio.incendio.dto.ChamadoItem;
import br.com.estacio.incendio.dto.ChamadoResumo;
import br.com.estacio.incendio.dto.NotificacaoDto;
import br.com.estacio.incendio.dto.SensorDoChamado;
import br.com.estacio.incendio.exception.RecursoNaoEncontradoException;
import br.com.estacio.incendio.exception.ValidacaoException;
import br.com.estacio.incendio.model.Chamado;
import br.com.estacio.incendio.model.LeituraSensor;
import br.com.estacio.incendio.model.Notificacao;
import br.com.estacio.incendio.model.Sensor;
import br.com.estacio.incendio.model.StatusChamado;
import br.com.estacio.incendio.repository.ChamadoRepository;
import br.com.estacio.incendio.repository.LeituraSensorRepository;
import br.com.estacio.incendio.repository.NotificacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ChamadoService {

    private static final Logger LOG = LoggerFactory.getLogger(ChamadoService.class);

    private final ChamadoRepository chamadoRepository;
    private final LeituraSensorRepository leituraRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final AvaliadorRisco avaliadorRisco;
    private final BombeiroService bombeiroService;

    public ChamadoService(ChamadoRepository chamadoRepository, LeituraSensorRepository leituraRepository,
                          NotificacaoRepository notificacaoRepository, AvaliadorRisco avaliadorRisco,
                          BombeiroService bombeiroService) {
        this.chamadoRepository = chamadoRepository;
        this.leituraRepository = leituraRepository;
        this.notificacaoRepository = notificacaoRepository;
        this.avaliadorRisco = avaliadorRisco;
        this.bombeiroService = bombeiroService;
    }

    @Transactional(readOnly = true)
    public List<ChamadoItem> listar(String status, String busca) {
        StatusChamado filtroStatus = parseStatus(status);
        String termo = busca == null ? "" : busca.trim().toLowerCase();
        return chamadoRepository.findAllByOrderByHorarioExatoDesc().stream()
                .filter(chamado -> filtroStatus == null || chamado.getStatus() == filtroStatus)
                .filter(chamado -> termo.isEmpty() || local(chamado).toLowerCase().contains(termo))
                .map(ChamadoItem::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChamadoResumo resumo() {
        return new ChamadoResumo(
                chamadoRepository.countByStatus(StatusChamado.NOVO),
                chamadoRepository.countByStatus(StatusChamado.EM_ATENDIMENTO),
                chamadoRepository.countByStatusAndEncerradoEmGreaterThanEqual(
                        StatusChamado.ENCERRADO, LocalDate.now().atStartOfDay()));
    }

    @Transactional(readOnly = true)
    public ChamadoDetalhe detalhe(Long id) {
        Chamado chamado = buscarEntidade(id);
        List<SensorDoChamado> sensores = chamado.getSensores().stream().map(this::paraSensorDoChamado).toList();
        List<NotificacaoDto> notificacoes = notificacaoRepository
                .findByChamadoIdOrderByEnviadaEmDesc(id).stream().map(this::paraNotificacao).toList();
        String responsavel = chamado.getBombeiroResponsavel() == null
                ? null : chamado.getBombeiroResponsavel().getNome();
        return new ChamadoDetalhe(
                chamado.getId(), ChamadoItem.numero(chamado.getId()), chamado.getStatus(),
                local(chamado),
                chamado.getLocalizacao() == null ? null : chamado.getLocalizacao().getLatitude(),
                chamado.getLocalizacao() == null ? null : chamado.getLocalizacao().getLongitude(),
                chamado.getHorarioExato(), chamado.getElevacaoTemperatura(),
                chamado.getTemperaturaMaxima(), responsavel, sensores, notificacoes);
    }

    public ChamadoDetalhe despachar(Long id, Long bombeiroId) {
        Chamado chamado = buscarEntidade(id);
        chamado.despachar(bombeiroService.buscarEntidade(bombeiroId));
        chamadoRepository.save(chamado);
        registrarLogNotificacoes(chamado);
        return detalhe(id);
    }

    public ChamadoDetalhe marcarEmAtendimento(Long id, Long bombeiroId) {
        Chamado chamado = buscarEntidade(id);
        chamado.marcarEmAtendimento(bombeiroService.buscarEntidade(bombeiroId));
        chamadoRepository.save(chamado);
        return detalhe(id);
    }

    public ChamadoDetalhe marcarFalsoAlarme(Long id) {
        Chamado chamado = buscarEntidade(id);
        chamado.marcarFalsoAlarme();
        chamadoRepository.save(chamado);
        return detalhe(id);
    }

    public ChamadoDetalhe encerrar(Long id) {
        Chamado chamado = buscarEntidade(id);
        chamado.encerrar();
        chamadoRepository.save(chamado);
        return detalhe(id);
    }

    @Transactional(readOnly = true)
    public Chamado buscarEntidade(Long id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chamado não encontrado: " + id));
    }

    private void registrarLogNotificacoes(Chamado chamado) {
        for (Notificacao notificacao : chamado.getNotificacoes()) {
            LOG.info("Notificação simulada [{}] para {}: {}", notificacao.getCanal(),
                    notificacao.getDestinatario().getNome(), notificacao.getMensagem());
        }
    }

    private SensorDoChamado paraSensorDoChamado(Sensor sensor) {
        LeituraSensor leitura = leituraRepository
                .findTopBySensorIdOrderByInstanteMedicaoDesc(sensor.getId()).orElse(null);
        return new SensorDoChamado(
                sensor.getId(), sensor.getLocalizacao(),
                leitura == null ? null : leitura.getTemperatura(),
                leitura == null ? null : leitura.getNivelFumaca(),
                leitura == null ? null : avaliadorRisco.rotuloFumaca(leitura.getNivelFumaca()).name(),
                sensor.getAtivo());
    }

    private NotificacaoDto paraNotificacao(Notificacao notificacao) {
        return new NotificacaoDto(
                notificacao.getId(), notificacao.getCanal(), notificacao.getEnviadaEm(),
                notificacao.getMensagem(), notificacao.getDestinatario().getNome());
    }

    private String local(Chamado chamado) {
        return chamado.getLocalizacao() == null ? "" : chamado.getLocalizacao().getNomeLocal();
    }

    private StatusChamado parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return StatusChamado.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidacaoException("Status de chamado inválido: " + status);
        }
    }
}
