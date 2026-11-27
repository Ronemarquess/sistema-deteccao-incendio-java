package br.com.estacio.incendio.model;

import br.com.estacio.incendio.exception.RegraNegocioException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chamados")
@Getter
@Setter
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusChamado status = StatusChamado.NOVO;

    @Column(name = "horario_exato", nullable = false)
    private LocalDateTime horarioExato;

    @Column(name = "temperatura_maxima")
    private Double temperaturaMaxima;

    @Column(name = "elevacao_temperatura")
    private Double elevacaoTemperatura;

    @Embedded
    private Localizacao localizacao;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chamado_sensor",
            joinColumns = @JoinColumn(name = "chamado_id"),
            inverseJoinColumns = @JoinColumn(name = "sensor_id")
    )
    private Set<Sensor> sensores = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bombeiro_responsavel_id")
    private Bombeiro bombeiroResponsavel;

    @Column(name = "encerrado_em")
    private LocalDateTime encerradoEm;

    @OneToMany(mappedBy = "chamado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacao> notificacoes = new ArrayList<>();

    public boolean estaFechado() {
        return status == StatusChamado.ENCERRADO || status == StatusChamado.FALSO_ALARME;
    }

    public boolean estaAberto() {
        return status == StatusChamado.NOVO || status == StatusChamado.EM_ATENDIMENTO;
    }

    public int getQuantidadeSensores() {
        return sensores.size();
    }

    public void adicionarSensor(Sensor sensor) {
        if (estaFechado()) {
            throw new RegraNegocioException("Chamado fechado não recebe novos sensores");
        }
        sensores.add(sensor);
    }

    public void despachar(Bombeiro bombeiro) {
        validarTransicao("despachar", StatusChamado.NOVO);
        this.bombeiroResponsavel = bombeiro;
        this.status = StatusChamado.EM_ATENDIMENTO;
        String destino = localizacao == null ? "local informado" : localizacao.getNomeLocal();
        Notificacao notificacao = new Notificacao(
                this, bombeiro, CanalNotificacao.PUSH,
                "Equipe despachada para " + destino + " - chamado #" + id);
        this.notificacoes.add(notificacao);
    }

    public void marcarEmAtendimento(Bombeiro bombeiro) {
        validarTransicao("marcar em atendimento", StatusChamado.NOVO);
        this.bombeiroResponsavel = bombeiro;
        this.status = StatusChamado.EM_ATENDIMENTO;
    }

    public void marcarFalsoAlarme() {
        if (estaFechado()) {
            throw new RegraNegocioException("Chamado já está fechado");
        }
        this.status = StatusChamado.FALSO_ALARME;
        this.encerradoEm = LocalDateTime.now();
    }

    public void encerrar() {
        if (status != StatusChamado.EM_ATENDIMENTO) {
            throw new RegraNegocioException(
                    "Só é possível encerrar um chamado EM_ATENDIMENTO; status atual: " + status);
        }
        this.status = StatusChamado.ENCERRADO;
        this.encerradoEm = LocalDateTime.now();
    }

    private void validarTransicao(String acao, StatusChamado esperado) {
        if (status != esperado) {
            throw new RegraNegocioException(
                    "Não é possível " + acao + " um chamado " + status + "; status esperado: " + esperado);
        }
    }
}
