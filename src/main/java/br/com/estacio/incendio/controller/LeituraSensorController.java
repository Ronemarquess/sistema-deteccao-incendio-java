package br.com.estacio.incendio.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.estacio.incendio.model.LeituraSensor;
import br.com.estacio.incendio.repository.LeituraSensorRepository;

@RestController
@RequestMapping("/api/leituras")
@CrossOrigin(origins = "*")
public class LeituraSensorController {
    private final LeituraSensorRepository leituraSensorRepository;

    public LeituraSensorController(LeituraSensorRepository leituraSensorRepository) {
        this.leituraSensorRepository = leituraSensorRepository;
    }

    @GetMapping
    public List<LeituraSensor> listarTodas() {
        return leituraSensorRepository.findAll();
    }
}
