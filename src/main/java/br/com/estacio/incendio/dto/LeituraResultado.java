package br.com.estacio.incendio.dto;

import br.com.estacio.incendio.model.NivelRisco;

public record LeituraResultado(
        LeituraDto leitura,
        NivelRisco nivelRisco,
        Long chamadoId,
        boolean chamadoCriado
) {
}
