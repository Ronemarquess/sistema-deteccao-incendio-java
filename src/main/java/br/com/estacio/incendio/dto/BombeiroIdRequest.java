package br.com.estacio.incendio.dto;

import jakarta.validation.constraints.NotNull;

public record BombeiroIdRequest(@NotNull Long bombeiroId) {
}
