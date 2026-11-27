package br.com.estacio.incendio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SensorRequest(
        @NotBlank String localizacao,
        @NotBlank String nomeLocal,
        @NotNull Double latitude,
        @NotNull Double longitude
) {
}
