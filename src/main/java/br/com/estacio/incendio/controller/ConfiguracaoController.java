package br.com.estacio.incendio.controller;

import br.com.estacio.incendio.config.Limites;
import br.com.estacio.incendio.dto.LimitesResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuracao")
public class ConfiguracaoController {

    private final Limites limites;

    public ConfiguracaoController(Limites limites) {
        this.limites = limites;
    }

    @GetMapping("/limites")
    public LimitesResponse limites() {
        return new LimitesResponse(
                limites.getTemperaturaAtencao(), limites.getTemperaturaAlerta(),
                limites.getTemperaturaCritica(), limites.getFumacaAtencao(),
                limites.getFumacaAlerta(), limites.getFumacaCritica());
    }
}
