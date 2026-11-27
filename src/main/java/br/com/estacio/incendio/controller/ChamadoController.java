package br.com.estacio.incendio.controller;

import br.com.estacio.incendio.dto.BombeiroIdRequest;
import br.com.estacio.incendio.dto.ChamadoDetalhe;
import br.com.estacio.incendio.dto.ChamadoItem;
import br.com.estacio.incendio.dto.ChamadoResumo;
import br.com.estacio.incendio.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoController {

    private final ChamadoService service;

    public ChamadoController(ChamadoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ChamadoItem> listar(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String busca) {
        return service.listar(status, busca);
    }

    @GetMapping("/resumo")
    public ChamadoResumo resumo() {
        return service.resumo();
    }

    @GetMapping("/{id}")
    public ChamadoDetalhe detalhe(@PathVariable Long id) {
        return service.detalhe(id);
    }

    @PostMapping("/{id}/despachar")
    public ChamadoDetalhe despachar(@PathVariable Long id, @RequestBody @Valid BombeiroIdRequest request) {
        return service.despachar(id, request.bombeiroId());
    }

    @PostMapping("/{id}/em-atendimento")
    public ChamadoDetalhe emAtendimento(@PathVariable Long id, @RequestBody @Valid BombeiroIdRequest request) {
        return service.marcarEmAtendimento(id, request.bombeiroId());
    }

    @PostMapping("/{id}/falso-alarme")
    public ChamadoDetalhe falsoAlarme(@PathVariable Long id) {
        return service.marcarFalsoAlarme(id);
    }

    @PostMapping("/{id}/encerrar")
    public ChamadoDetalhe encerrar(@PathVariable Long id) {
        return service.encerrar(id);
    }
}
