package com.ex.self_alert.repository.sensorrepository;


import com.ex.self_alert.model.sensormodel.Sensor;
import com.ex.self_alert.model.sensormodel.StatusSensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface SensorRepository extends JpaRepository <Sensor, UUID>{

    List<Sensor> findByStatus(StatusSensor status);

    long countByStatus(StatusSensor status);
}
