package br.com.estacio.incendio.controller;

import br.com.estacio.incendio.service.SimulacaoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/simulacao")
public class SimulacaoController {

    private final SimulacaoService service;

    public SimulacaoController(SimulacaoService service) {
        this.service = service;
    }

    @PostMapping("/reset")
    public Map<String, Object> reset() {
        service.reset();
        return Map.of("resetado", true, "mensagem", "Demonstração reiniciada");
    }
}
