package com.ex.self_alert.controller.sensor;



import com.ex.self_alert.dto.sensor.SensorResponse;
import com.ex.self_alert.service.sensor.SimuladorService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/simulador")
public class SimuladorController {
    

    private final SimuladorService
    service;

    public SimuladorController(SimuladorService service){
        this.service = service;
    }

    @GetMapping("/status")
    public Map<String, Boolean> status(){
        return Map.of("ligado", service.estaLigado());
    }

    @PostMapping ("/ligar")
    public Map<String, Boolean> ligar(){
        service.ligar();
        return Map.of("ligado", true);
    }

    @PostMapping("/desligar")
    public Map<String, Boolean> desligar(){
        service.desligar();
        return Map.of("ligado", false);
    }

    @PostMapping("/sensores/{Id}/incendio")
    public SensorResponse focarIncendio(@PathVariable UUID Id){
        return service.focarIncendio(Id);
    }
    
}