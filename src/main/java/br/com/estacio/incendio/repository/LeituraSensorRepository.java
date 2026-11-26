package br.com.estacio.incendio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estacio.incendio.model.LeituraSensor;

public interface LeituraSensorRepository extends JpaRepository<LeituraSensor, Long> {
}
