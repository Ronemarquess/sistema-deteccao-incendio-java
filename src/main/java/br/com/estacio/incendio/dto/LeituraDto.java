package br.com.estacio.incendio.dto;

import br.com.estacio.incendio.model.LeituraSensor;
import br.com.estacio.incendio.model.NivelRisco;

import java.time.LocalDateTime;

public record LeituraDto(
        Long id,
        Long sensorId,
        Double temperatura,
        Double nivelFumaca,
        Double umidade,
        LocalDateTime instanteMedicao,
        NivelRisco nivelRisco
) {
    public static LeituraDto de(LeituraSensor leitura) {
        return new LeituraDto(
                leitura.getId(), leitura.getSensor().getId(), leitura.getTemperatura(),
                leitura.getNivelFumaca(), leitura.getUmidade(), leitura.getInstanteMedicao(),
                leitura.getNivelRisco());
    }
}
