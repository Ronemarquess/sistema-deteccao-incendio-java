package br.com.estacio.incendio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bombeiros")
@Getter
@Setter
public class Bombeiro extends Usuario {

    private String matricula;
    private String patente;
    private String batalhao;
    private String telefone;
}
