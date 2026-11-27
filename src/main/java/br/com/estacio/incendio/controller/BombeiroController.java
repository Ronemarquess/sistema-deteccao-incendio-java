package br.com.estacio.incendio.controller;

import br.com.estacio.incendio.dto.AtivoRequest;
import br.com.estacio.incendio.dto.BombeiroRequest;
import br.com.estacio.incendio.dto.BombeiroResponse;
import br.com.estacio.incendio.dto.LoginRequest;
import br.com.estacio.incendio.service.BombeiroService;
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
@RequestMapping("/api/bombeiros")
public class BombeiroController {

    private final BombeiroService service;

    public BombeiroController(BombeiroService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public BombeiroResponse login(@RequestBody @Valid LoginRequest request) {
        return service.login(request.email(), request.senha());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BombeiroResponse cadastrar(@RequestBody @Valid BombeiroRequest request) {
        return service.cadastrar(request);
    }

    @GetMapping
    public List<BombeiroResponse> listar() {
        return service.listar();
    }

    @PutMapping("/{id}")
    public BombeiroResponse atualizar(@PathVariable Long id, @RequestBody @Valid BombeiroRequest request) {
        return service.atualizar(id, request);
    }

    @PatchMapping("/{id}/ativo")
    public BombeiroResponse definirAtivo(@PathVariable Long id, @RequestBody @Valid AtivoRequest request) {
        return service.definirAtivo(id, request.ativo());
    }
}
