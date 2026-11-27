package com.ex.self_alert.model.sensor;


import java.util.Random;
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

    @Override
    public double simularLeitura(Random random, boolean pico){
        if(pico){
            return nivelFumacaLimite + 20 + random.nextInt(300);
        }
        return 50 + random.nextInt(150);
    }

    @Override
    public TipoSensor tipo(){
        return TipoSensor.FUMACA;
    }

    @Override
    public Double valorAtual(){
        return nivelFumacaAtual != null ? nivelFumacaAtual.doubleValue() : null;
    }

}
