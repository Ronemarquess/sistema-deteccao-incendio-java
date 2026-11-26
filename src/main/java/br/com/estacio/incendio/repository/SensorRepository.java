package br.com.estacio.incendio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estacio.incendio.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
}
