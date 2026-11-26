package br.com.estacio.incendio.dto;

import br.com.estacio.incendio.model.Sensor;

public record SensorResponseDTO(
        Long id,
        String tipo,
        String localizacao
) {
    public static SensorResponseDTO fromEntity(Sensor sensor) {
        return new SensorResponseDTO(sensor.getId(), sensor.getTipo(), sensor.getLocalizacao());
    }
}
