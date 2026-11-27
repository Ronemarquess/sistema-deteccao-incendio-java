package br.com.estacio.incendio.dto;

import br.com.estacio.incendio.model.Chamado;
import br.com.estacio.incendio.model.StatusChamado;

import java.time.LocalDateTime;

public record ChamadoItem(
        Long id,
        String numero,
        String local,
        int quantidadeSensores,
        Double temperaturaMaxima,
        StatusChamado status,
        LocalDateTime horarioExato
) {
    public static ChamadoItem de(Chamado chamado) {
        String local = chamado.getLocalizacao() == null
                ? "" : chamado.getLocalizacao().getNomeLocal();
        return new ChamadoItem(
                chamado.getId(), numero(chamado.getId()), local,
                chamado.getQuantidadeSensores(), chamado.getTemperaturaMaxima(),
                chamado.getStatus(), chamado.getHorarioExato());
    }

    public static String numero(Long id) {
        return String.format("#%03d", id == null ? 0 : id);
    }
}
