package br.com.estacio.incendio.dto;

public record SensorDoChamado(
        Long id,
        String localizacao,
        Double temperatura,
        Double nivelFumaca,
        String nivelFumacaRotulo,
        Boolean ativo
) {
}
