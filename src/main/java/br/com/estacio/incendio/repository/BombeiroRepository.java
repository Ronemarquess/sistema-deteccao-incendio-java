package br.com.estacio.incendio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estacio.incendio.model.Bombeiro;

public interface BombeiroRepository extends JpaRepository<Bombeiro, Long> {
    Optional<Bombeiro> findByEmail(String email);

    Optional<Bombeiro> findByMatricula(String matricula);

    boolean existsByEmail(String email);

    boolean existsByMatricula(String matricula);

    List<Bombeiro> findByBatalhaoIgnoreCase(String batalhao);

    List<Bombeiro> findByAtivoTrue();
}
