package com.ex.self_alert.dto.sensor;


import com.ex.self_alert.model.sensor.TipoSensor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SensorRequest(
    @NotBlank String local,
    @NotNull  Double latitude,
    @NotNull Double longitude,
    @NotNull TipoSensor tipo
)
{}