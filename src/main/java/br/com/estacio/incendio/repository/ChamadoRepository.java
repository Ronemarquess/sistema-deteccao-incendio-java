package br.com.estacio.incendio.repository;

import br.com.estacio.incendio.model.Chamado;
import br.com.estacio.incendio.model.StatusChamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    List<Chamado> findAllByOrderByHorarioExatoDesc();

    List<Chamado> findByStatusInAndLocalizacaoNomeLocalOrderByHorarioExatoDesc(
            Collection<StatusChamado> status, String nomeLocal);

    long countByStatus(StatusChamado status);

    long countByStatusAndEncerradoEmGreaterThanEqual(StatusChamado status, LocalDateTime inicio);
}
