package br.com.estacio.incendio.controller;

import br.com.estacio.incendio.dto.AtivoRequest;
import br.com.estacio.incendio.dto.SensorRequest;
import br.com.estacio.incendio.dto.SensorResumo;
import br.com.estacio.incendio.dto.SensorResponse;
import br.com.estacio.incendio.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sensores")
public class SensorController {

    private final SensorService service;

    public SensorController(SensorService service) {
        this.service = service;
    }

    @GetMapping
    public List<SensorResponse> listar() {
        return service.listar();
    }

    @GetMapping("/resumo")
    public SensorResumo resumo() {
        return service.resumo();
    }

    @GetMapping("/{id}")
    public SensorResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorResponse criar(@RequestBody @Valid SensorRequest request) {
        return service.criar(request);
    }

    @PutMapping("/{id}")
    public SensorResponse atualizar(@PathVariable Long id, @RequestBody @Valid SensorRequest request) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}/ativo")
    public SensorResponse definirAtivo(@PathVariable Long id, @RequestBody @Valid AtivoRequest request) {
        return service.definirAtivo(id, request);
    }
}
