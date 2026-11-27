package br.com.estacio.incendio.dto;

import java.time.Instant;
import java.util.Map;

public record ErroResponse(
        Instant timestamp,
        int status,
        String erro,
        String mensagem,
        Map<String, String> campos
) {
}
