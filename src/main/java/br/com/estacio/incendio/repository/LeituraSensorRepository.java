package br.com.estacio.incendio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import br.com.estacio.incendio.model.LeituraSensor;

public interface LeituraSensorRepository extends JpaRepository<LeituraSensor, Long> {
    @Override
    @EntityGraph(attributePaths = "sensor")
    List<LeituraSensor> findAll();
}
