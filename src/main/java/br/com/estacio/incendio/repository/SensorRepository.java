package br.com.estacio.incendio.repository;

import br.com.estacio.incendio.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    boolean existsByLocalizacaoAndLocalNomeLocal(String localizacao, String nomeLocal);
}
