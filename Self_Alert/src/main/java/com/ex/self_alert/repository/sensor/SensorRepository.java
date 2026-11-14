package com.ex.self_alert.repository.sensor;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.self_alert.model.sensor.Sensor;
import com.ex.self_alert.model.sensor.StatusSensor;

import java.util.List;
import java.util.UUID;


public interface SensorRepository extends JpaRepository <Sensor, UUID>{

    List<Sensor> findByStatus(StatusSensor status);

    long countByStatus(StatusSensor status);
}
