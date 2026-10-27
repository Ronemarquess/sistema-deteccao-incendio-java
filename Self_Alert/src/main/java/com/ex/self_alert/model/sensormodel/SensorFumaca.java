package com.ex.self_alert.model.sensormodel;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("FUMACA")
@Getter 
@Setter 
public class SensorFumaca extends Sensor {

    private Integer nivelFumacaAtual;
    private Integer nivelFumacaLimite;

    @Override 
    public boolean estaEmAlerta() {
        return nivelFumacaAtual != null && nivelFumacaAtual >= nivelFumacaLimite;
    }

    @Override 
    public void registrarLeitura(Double valor){
        this.nivelFumacaAtual = valor.intValue();
    }

}
