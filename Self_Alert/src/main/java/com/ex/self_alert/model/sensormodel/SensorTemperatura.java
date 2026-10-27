package com.ex.self_alert.model.sensormodel;

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
}
