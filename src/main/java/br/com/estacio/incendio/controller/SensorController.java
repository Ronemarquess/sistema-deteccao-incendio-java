package br.com.estacio.incendio.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.estacio.incendio.dto.SensorResponseDTO;
import br.com.estacio.incendio.repository.SensorRepository;

@RestController
@RequestMapping("/api/sensores")
@CrossOrigin(origins = {"http://localhost:5173", "https://frontend-incendio.vercel.app"})
public class SensorController {
    private final SensorRepository sensorRepository;

    public SensorController(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @GetMapping
    public List<SensorResponseDTO> listarTodos() {
        return sensorRepository.findAll().stream().map(SensorResponseDTO::fromEntity).toList();
    }
}
