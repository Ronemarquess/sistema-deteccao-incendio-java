package br.com.estacio.incendio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Localizacao {

    @Column(name = "nome_local")
    private String nomeLocal;

    private Double latitude;

    private Double longitude;

    public static Localizacao de(String nomeLocal, Double latitude, Double longitude) {
        return new Localizacao(nomeLocal, latitude, longitude);
    }
}
