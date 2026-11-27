package br.com.estacio.incendio.dto;

public record LimitesResponse(
        double temperaturaAtencao,
        double temperaturaAlerta,
        double temperaturaCritica,
        double fumacaAtencao,
        double fumacaAlerta,
        double fumacaCritica
) {
}
