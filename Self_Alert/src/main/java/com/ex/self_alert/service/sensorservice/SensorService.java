package com.ex.self_alert.service.sensorservice;

import com.ex.self_alert.dto.LeituraRequest;
import com.ex.self_alert.dto.SensorRequest;
import com.ex.self_alert.dto.SensorResponse;
import com.ex.self_alert.exception.RecursoNaoEncontradoException;
import com.ex.self_alert.model.sensormodel.Sensor;
import com.ex.self_alert.model.sensormodel.SensorFumaca;
import com.ex.self_alert.model.sensormodel.SensorTemperatura;
import com.ex.self_alert.repository.sensorrepository.SensorRepository;
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
        return repository.findAll().stream().map(SensorResponse::de).toList();
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

    public void deletar(UUID id){
        repository.delete(buscarEntidade(id));
    }

    public Sensor buscarEntidade(UUID id){
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Sensor não encontrado  " + id));
    }

}
