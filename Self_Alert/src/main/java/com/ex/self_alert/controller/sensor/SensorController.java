package com.ex.self_alert.controller.sensor;


import com.ex.self_alert.dto.sensor.LeituraRequest;
import com.ex.self_alert.dto.sensor.SensorRequest;
import com.ex.self_alert.dto.sensor.SensorResponse;
import com.ex.self_alert.service.sensor.SensorService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sensores")

public class SensorController{

  

    private final SensorService service;
    public SensorController(SensorService service){
        this.service = service;
    }

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public SensorResponse criarSensor(@RequestBody @Valid SensorRequest request){
        return service.criar(request);
    }

    @GetMapping("/{id}")
    public SensorResponse buscarSensor(@PathVariable UUID id){
        return service.buscar(id);
    }

    @GetMapping
    public List<SensorResponse> listar(){
        return service.findAll();
    }

    @PutMapping("/{id}")
    public SensorResponse atualizar(@PathVariable UUID id, @RequestBody @Valid SensorRequest request){
        return service.atualizar(id, request);
    }
    @PatchMapping("/{id}/leitura")
    public SensorResponse registrarLeitura(@PathVariable UUID id, @RequestBody @Valid LeituraRequest request){
        return service.registrarLeitura(id, request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable UUID id){
        service.remove(id);
    }
}
