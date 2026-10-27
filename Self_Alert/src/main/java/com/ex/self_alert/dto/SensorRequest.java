package com.ex.self_alert.dto;


import com.ex.self_alert.model.sensormodel.TipoSensor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SensorRequest(
    @NotBlank String local,
    @NotNull  Double latitude,
    @NotNull Double longitude,
    @NotBlank TipoSensor tipo
)
{}