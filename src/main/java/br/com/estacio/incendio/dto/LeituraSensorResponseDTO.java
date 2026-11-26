package br.com.estacio.incendio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.estacio.incendio.model.LeituraSensor;

public record LeituraSensorResponseDTO(
        Long id,
        BigDecimal valor,
        LocalDateTime instanteMedicao,
        SensorResponseDTO sensor
) {
    public static LeituraSensorResponseDTO fromEntity(LeituraSensor leitura) {
        return new LeituraSensorResponseDTO(
                leitura.getId(),
                leitura.getValor(),
                leitura.getInstanteMedicao(),
                SensorResponseDTO.fromEntity(leitura.getSensor())
        );
    }
}
