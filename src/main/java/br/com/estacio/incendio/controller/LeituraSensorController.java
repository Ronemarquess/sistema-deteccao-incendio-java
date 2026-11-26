package br.com.estacio.incendio.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.estacio.incendio.dto.LeituraSensorResponseDTO;
import br.com.estacio.incendio.repository.LeituraSensorRepository;

@RestController
@RequestMapping("/api/leituras")
public class LeituraSensorController {
    private final LeituraSensorRepository leituraSensorRepository;

    public LeituraSensorController(LeituraSensorRepository leituraSensorRepository) {
        this.leituraSensorRepository = leituraSensorRepository;
    }

    @GetMapping
    public List<LeituraSensorResponseDTO> listarTodas() {
        return leituraSensorRepository.findAll().stream()
                .map(LeituraSensorResponseDTO::fromEntity)
                .toList();
    }
}
