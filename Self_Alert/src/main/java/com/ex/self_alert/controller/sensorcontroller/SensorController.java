package com.ex.self_alert.controller.sensorcontroller;


import com.ex.self_alert.dto.LeituraRequest;
import com.ex.self_alert.dto.SensorRequest;
import com.ex.self_alert.dto.SensorResponse;
import com.ex.self_alert.service.sensorservice.SensorService;
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

    @PutMapping("/{id}")
    public SensorResponse atualizar(@PathVariable UUID id, @RequestBody @Valid SensorRequest request){
        return service.atualizar(id, request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id){
        service.deletar(id);
    }
}
