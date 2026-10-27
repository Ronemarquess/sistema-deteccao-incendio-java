package com.ex.self_alert.dto;

import com.ex.self_alert.model.sensormodel.Sensor;
import com.ex.self_alert.model.sensormodel.SensorTemperatura;
import com.ex.self_alert.model.sensormodel.StatusSensor;
import com.ex.self_alert.model.sensormodel.TipoSensor;

import java.time.LocalDateTime;
import java.util.UUID;

public record SensorResponse(
        UUID id,
        String local,
        Double latitude,
        Double longitude,
        TipoSensor tipo,
        StatusSensor status,
        boolean emAlerta,
        LocalDateTime ultimaLeitura
) {
    public static SensorResponse de(Sensor sensor) {
        TipoSensor tipo = sensor instanceof SensorTemperatura
                ? TipoSensor.TEMPERATURA
                : TipoSensor.FUMACA;

        return new SensorResponse(
                sensor.getId(),
                sensor.getLocal(),
                sensor.getLatitude(),
                sensor.getLongitude(),
                tipo,
                sensor.getStatus(),
                sensor.estaEmAlerta(),
                sensor.getUltimaLeitura()
        );
    }
}