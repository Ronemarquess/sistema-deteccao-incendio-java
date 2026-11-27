package br.com.estacio.incendio.repository;

import br.com.estacio.incendio.model.LeituraSensor;
import br.com.estacio.incendio.model.NivelRisco;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeituraSensorRepository extends JpaRepository<LeituraSensor, Long> {

    List<LeituraSensor> findBySensorIdOrderByInstanteMedicaoDesc(Long sensorId, Pageable pageable);

    Optional<LeituraSensor> findTopBySensorIdOrderByInstanteMedicaoDesc(Long sensorId);

    List<LeituraSensor> findTop5BySensorIdAndNivelRiscoAndInstanteMedicaoBeforeOrderByInstanteMedicaoDesc(
            Long sensorId, NivelRisco nivelRisco, LocalDateTime antes);
}
