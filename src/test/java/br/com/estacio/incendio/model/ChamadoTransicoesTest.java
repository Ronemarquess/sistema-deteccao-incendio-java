package br.com.estacio.incendio.model;

import br.com.estacio.incendio.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChamadoTransicoesTest {

    private final Bombeiro bombeiro = new Bombeiro();

    private Chamado novoChamado() {
        Chamado chamado = new Chamado();
        chamado.setId(1L);
        chamado.setStatus(StatusChamado.NOVO);
        chamado.setHorarioExato(LocalDateTime.now());
        return chamado;
    }

    @Test
    void despacharDefineResponsavelENotificacao() {
        Chamado chamado = novoChamado();
        chamado.despachar(bombeiro);
        assertEquals(StatusChamado.EM_ATENDIMENTO, chamado.getStatus());
        assertEquals(bombeiro, chamado.getBombeiroResponsavel());
        assertEquals(1, chamado.getNotificacoes().size());
        assertEquals(CanalNotificacao.PUSH, chamado.getNotificacoes().get(0).getCanal());
    }

    @Test
    void marcarEmAtendimentoNaoCriaNotificacao() {
        Chamado chamado = novoChamado();
        chamado.marcarEmAtendimento(bombeiro);
        assertEquals(StatusChamado.EM_ATENDIMENTO, chamado.getStatus());
        assertEquals(bombeiro, chamado.getBombeiroResponsavel());
        assertTrue(chamado.getNotificacoes().isEmpty());
    }

    @Test
    void encerrarSomenteAposAtendimento() {
        Chamado chamado = novoChamado();
        assertThrows(RegraNegocioException.class, chamado::encerrar);
        chamado.marcarEmAtendimento(bombeiro);
        chamado.encerrar();
        assertEquals(StatusChamado.ENCERRADO, chamado.getStatus());
        assertNotNull(chamado.getEncerradoEm());
    }

    @Test
    void falsoAlarmeFechaChamado() {
        Chamado chamado = novoChamado();
        chamado.marcarFalsoAlarme();
        assertEquals(StatusChamado.FALSO_ALARME, chamado.getStatus());
        assertThrows(RegraNegocioException.class, chamado::encerrar);
        assertThrows(RegraNegocioException.class, () -> chamado.marcarEmAtendimento(bombeiro));
    }

    @Test
    void chamadoFechadoNaoRecebeSensores() {
        Chamado chamado = novoChamado();
        chamado.adicionarSensor(new Sensor());
        assertEquals(1, chamado.getQuantidadeSensores());
        chamado.marcarFalsoAlarme();
        assertThrows(RegraNegocioException.class, () -> chamado.adicionarSensor(new Sensor()));
    }
}
