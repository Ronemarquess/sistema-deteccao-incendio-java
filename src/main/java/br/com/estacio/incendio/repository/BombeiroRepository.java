package br.com.estacio.incendio.repository;

import br.com.estacio.incendio.model.Bombeiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BombeiroRepository extends JpaRepository<Bombeiro, Long> {

    Optional<Bombeiro> findByEmail(String email);

    boolean existsByEmail(String email);
}
