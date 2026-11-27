package com.ex.self_alert.dto.sensor;

import jakarta.validation.constraints.NotNull;
public record LeituraRequest(
    @NotNull Double valor
){}

