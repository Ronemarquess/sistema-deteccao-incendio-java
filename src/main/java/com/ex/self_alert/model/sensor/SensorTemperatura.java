package com.ex.self_alert.model.sensor;

import java.util.Random;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


@Entity 
@DiscriminatorValue("TEMPERATURA")
@Getter
@Setter


public class SensorTemperatura extends Sensor{
    
    private Double temperaturaAtual;
    private Double temperaturaLimite = 60.0;

    @Override
    public boolean estaEmAlerta(){
        return temperaturaAtual != null && temperaturaAtual > temperaturaLimite;
    }

    @Override
    public void registrarLeitura(Double valor){
        this.temperaturaAtual = valor;
    }

    @Override
    public double simularLeitura(Random random, boolean pico){
        if(pico){
            return temperaturaLimite + 5 + random.nextDouble() * 25;   
             }
             return 22 + random.nextDouble() * 10;
    }

    @Override 
    public TipoSensor tipo(){
        return TipoSensor.TEMPERATURA;
    }

    @Override 
    public Double valorAtual(){
        return temperaturaAtual;
    }
    
}
