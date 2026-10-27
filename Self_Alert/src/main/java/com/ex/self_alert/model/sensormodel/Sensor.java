package com.ex.self_alert.model.sensormodel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sensores")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType=DiscriminatorType.STRING)
@Getter 
@Setter 
public abstract class Sensor {

    @Id 
    private UUID id;

    @Column(nullable = false)
    private String local;

    @Column(nullable = false)
    private Double latitude;

    
    @Column(nullable = false)
    private Double longitude;
    
    @Enumerated (EnumType.STRING)
    @Column(nullable = false)
    private StatusSensor status = StatusSensor.ATIVO;

    private LocalDateTime ultimaLeitura;

    public abstract boolean estaEmAlerta();

    public abstract void registrarLeitura(Double valor);
}
