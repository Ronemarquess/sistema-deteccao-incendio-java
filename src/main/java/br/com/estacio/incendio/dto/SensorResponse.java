package br.com.estacio.incendio.dto;

import java.time.LocalDateTime;

public record SensorResponse(
        Long id,
        String localizacao,
        String nomeLocal,
        Double latitude,
        Double longitude,
        Boolean ativo,
        Double temperatura,
        Double nivelFumaca,
        String nivelFumacaRotulo,
        LocalDateTime instanteMedicao
) {
}
