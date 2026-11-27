package com.ex.self_alert.service.sensor;


import com.ex.self_alert.dto.sensor.LeituraRequest;
import com.ex.self_alert.dto.sensor.SensorResponse;
import com.ex.self_alert.exception.RecursoNaoEncontradoException;
import com.ex.self_alert.model.sensor.Sensor;
import com.ex.self_alert.model.sensor.StatusSensor;
import com.ex.self_alert.repository.sensor.SensorRepository;
import com.ex.self_alert.service.sensor.SensorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SimuladorService {

    private final SensorRepository sensorRepository;
    private final SensorService sensorService;
    private final Random random = new Random();
    private final AtomicBoolean ligado;
    private final double probabilidadePico;

    public SimuladorService(SensorRepository sensorRepository, SensorService sensorService, @Value("${simulador.ativo:false}") boolean ativoInicial,@Value("${simulador.probabilidade-pico:0.05}") double probabilidadePico){

        this.sensorRepository = sensorRepository;
        this.sensorService = sensorService;
        this.ligado = new AtomicBoolean(ativoInicial);
        this.probabilidadePico = probabilidadePico;
    }

    @Scheduled(fixedDelayString = "${simulador.intervalo-ms:5000}")
    public void gerarLeitura(){
        if(!ligado.get()){
            return;
        }
        for(Sensor sensor : sensorRepository.findByStatus(StatusSensor.ATIVO)){
            boolean pico = random.nextDouble() < probabilidadePico;
            enviarLeitura(sensor, pico);
        }
    }

    public SensorResponse focarIncendio(UUID sensorId){
        Sensor sensor = sensorRepository.findById(sensorId).orElseThrow(() -> new RecursoNaoEncontradoException("Sensor não encontrado: "+ sensorId));

        return enviarLeitura(sensor, true);

    }

    public void ligar(){
        ligado.set(true);

    }

    public void desligar(){
        ligado.set(false);
    }

    public boolean estaLigado(){
        return ligado.get();
    }

    private SensorResponse enviarLeitura(Sensor sensor, boolean pico){

        double valor = Math.round(sensor.simularLeitura(random,pico) * 10.0) /10.0;


        return sensorService.registrarLeitura(sensor.getId(), new LeituraRequest(valor));
    }
}
