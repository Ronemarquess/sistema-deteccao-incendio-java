package br.com.estacio.incendio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "bombeiros", uniqueConstraints = {
        @UniqueConstraint(name = "uk_bombeiros_matricula", columnNames = "matricula")
})
public class Bombeiro extends Usuario {
    @Column(nullable = false)
    private String matricula;

    @Column(nullable = false)
    private String patente;

    @Column(nullable = false)
    private String batalhao;

    @Column(nullable = false)
    private String telefone;

    public Bombeiro() {
        super();
    }

    public Bombeiro(Long id, String nome, String email, String senha, Boolean ativo,
                    String matricula, String patente, String batalhao, String telefone) {
        super(id, nome, email, senha, ativo);
        this.matricula = matricula;
        this.patente = patente;
        this.batalhao = batalhao;
        this.telefone = telefone;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getBatalhao() {
        return batalhao;
    }

    public void setBatalhao(String batalhao) {
        this.batalhao = batalhao;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public void exibirPerfil() {
        System.out.println("Patente: " + patente);
        System.out.println("Nome: " + getNome());
        System.out.println("Matrícula: " + matricula);
    }
}