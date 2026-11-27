package br.com.estacio.incendio.repository;

import br.com.estacio.incendio.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByChamadoIdOrderByEnviadaEmDesc(Long chamadoId);
}
