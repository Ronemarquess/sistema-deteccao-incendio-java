package br.com.estacio.incendio.dto;

import br.com.estacio.incendio.model.StatusChamado;

import java.time.LocalDateTime;
import java.util.List;

public record ChamadoDetalhe(
        Long id,
        String numero,
        StatusChamado status,
        String local,
        Double latitude,
        Double longitude,
        LocalDateTime horarioExato,
        Double elevacaoTemperatura,
        Double temperaturaMaxima,
        String bombeiroResponsavel,
        List<SensorDoChamado> sensores,
        List<NotificacaoDto> notificacoes
) {
}
