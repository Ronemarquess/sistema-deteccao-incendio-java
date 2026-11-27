package br.com.estacio.incendio.controller;

import br.com.estacio.incendio.dto.LeituraDto;
import br.com.estacio.incendio.dto.LeituraRequest;
import br.com.estacio.incendio.dto.LeituraResultado;
import br.com.estacio.incendio.service.LeituraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leituras")
public class LeituraController {

    private final LeituraService service;

    public LeituraController(LeituraService service) {
        this.service = service;
    }

    @GetMapping
    public List<LeituraDto> listar(
            @RequestParam(required = false) Long sensorId,
            @RequestParam(defaultValue = "50") int limite) {
        return service.listar(sensorId, limite);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeituraResultado registrar(@RequestBody @Valid LeituraRequest request) {
        return service.registrar(request);
    }
}
