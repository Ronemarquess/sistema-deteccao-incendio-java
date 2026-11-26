package br.com.estacio.incendio.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import br.com.estacio.incendio.dto.BombeiroRequestDTO;
import br.com.estacio.incendio.dto.BombeiroResponseDTO;
import br.com.estacio.incendio.dto.LoginRequestDTO;
import br.com.estacio.incendio.service.BombeiroService;

@RestController
@RequestMapping("/api/bombeiros")
public class BombeiroController {
    private final BombeiroService bombeiroService;

    public BombeiroController(BombeiroService bombeiroService) {
        this.bombeiroService = bombeiroService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BombeiroResponseDTO cadastrar(@Valid @RequestBody BombeiroRequestDTO request) {
        return BombeiroResponseDTO.fromEntity(bombeiroService.cadastrar(request));
    }

    @PostMapping("/login")
    public BombeiroResponseDTO autenticar(@Valid @RequestBody LoginRequestDTO request) {
        return BombeiroResponseDTO.fromEntity(bombeiroService.autenticar(request));
    }

    @GetMapping
    public List<BombeiroResponseDTO> listarTodos() {
        return bombeiroService.listarTodos().stream().map(BombeiroResponseDTO::fromEntity).toList();
    }

    @GetMapping("/{id}")
    public BombeiroResponseDTO buscarPorId(@PathVariable Long id) {
        return BombeiroResponseDTO.fromEntity(bombeiroService.buscarPorId(id));
    }

    @GetMapping("/matricula/{matricula}")
    public BombeiroResponseDTO buscarPorMatricula(@PathVariable String matricula) {
        return BombeiroResponseDTO.fromEntity(bombeiroService.buscarPorMatricula(matricula));
    }

    @PutMapping("/{id}")
    public BombeiroResponseDTO atualizar(
            @PathVariable Long id,
            @Valid @RequestBody BombeiroRequestDTO request) {
        return BombeiroResponseDTO.fromEntity(bombeiroService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        bombeiroService.desativar(id);
    }
}
