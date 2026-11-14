package com.ex.self_alert.service.sensor;

import com.ex.self_alert.dto.sensor.LeituraRequest;
import com.ex.self_alert.dto.sensor.SensorRequest;
import com.ex.self_alert.dto.sensor.SensorResponse;
import com.ex.self_alert.exception.RecursoNaoEncontradoException;
import com.ex.self_alert.model.sensor.Sensor;
import com.ex.self_alert.model.sensor.SensorFumaca;
import com.ex.self_alert.model.sensor.SensorTemperatura;
import com.ex.self_alert.repository.sensor.SensorRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service 
public class SensorService{

    private final SensorRepository repository;

    public SensorService(SensorRepository repository){
        this.repository = repository;

    }
    public SensorResponse 
    criar(SensorRequest request){
        Sensor sensor = switch(request.tipo()){
            case TEMPERATURA -> new SensorTemperatura();
            case FUMACA -> new SensorFumaca();
        };
        sensor.setId(UUID.randomUUID());
        sensor.setLocal(request.local());
        sensor.setLatitude(request.latitude());
        sensor.setLongitude(request.longitude());

        return SensorResponse.de(repository.save(sensor));
    }

    public List<SensorResponse> findAll(){
     return repository.findAll().stream().map(SensorResponse::de).collect(java.util.stream.Collectors.toList());
    }

    public SensorResponse buscar(UUID id){
        return SensorResponse.de(buscarEntidade(id));
    }

    public SensorResponse atualizar(UUID id, SensorRequest request){

        Sensor sensor = buscarEntidade(id);
        sensor.setLocal(request.local());
        sensor.setLatitude(request.latitude());
        sensor.setLongitude(request.longitude());

        return SensorResponse.de(repository.save(sensor));
    }

    public SensorResponse registrarLeitura(UUID id, LeituraRequest request){
        Sensor sensor = buscarEntidade(id);
        sensor.registrarLeitura(request.valor());
        sensor.setUltimaLeitura(LocalDateTime.now());

        return SensorResponse.de(repository.save(sensor));
    }

    public void remove(UUID id){
        repository.delete(buscarEntidade(id));
    }

    public Sensor buscarEntidade(UUID id){
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Sensor não encontrado  " + id));
    }

}
