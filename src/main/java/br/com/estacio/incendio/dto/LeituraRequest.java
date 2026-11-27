package br.com.estacio.incendio.dto;

import jakarta.validation.constraints.NotNull;

public record LeituraRequest(
        @NotNull Long sensorId,
        @NotNull Double temperatura,
        @NotNull Double nivelFumaca,
        Double umidade
) {
}
