package br.com.estacio.incendio.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class LeituraSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal valor;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime instanteMedicao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    public LeituraSensor() {
    }

    public LeituraSensor(BigDecimal valor, LocalDateTime instanteMedicao, Sensor sensor) {
        this.valor = valor;
        this.instanteMedicao = instanteMedicao;
        this.sensor = sensor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getInstanteMedicao() {
        return instanteMedicao;
    }

    public void setInstanteMedicao(LocalDateTime instanteMedicao) {
        this.instanteMedicao = instanteMedicao;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
