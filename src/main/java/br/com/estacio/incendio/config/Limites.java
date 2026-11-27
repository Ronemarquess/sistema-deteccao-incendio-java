package br.com.estacio.incendio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "safealert.limites")
public class Limites {

    private double temperaturaAtencao = 40;
    private double temperaturaAlerta = 57;
    private double temperaturaCritica = 80;
    private double fumacaAtencao = 30;
    private double fumacaAlerta = 50;
    private double fumacaCritica = 85;

    public double getTemperaturaAtencao() {
        return temperaturaAtencao;
    }

    public void setTemperaturaAtencao(double temperaturaAtencao) {
        this.temperaturaAtencao = temperaturaAtencao;
    }

    public double getTemperaturaAlerta() {
        return temperaturaAlerta;
    }

    public void setTemperaturaAlerta(double temperaturaAlerta) {
        this.temperaturaAlerta = temperaturaAlerta;
    }

    public double getTemperaturaCritica() {
        return temperaturaCritica;
    }

    public void setTemperaturaCritica(double temperaturaCritica) {
        this.temperaturaCritica = temperaturaCritica;
    }

    public double getFumacaAtencao() {
        return fumacaAtencao;
    }

    public void setFumacaAtencao(double fumacaAtencao) {
        this.fumacaAtencao = fumacaAtencao;
    }

    public double getFumacaAlerta() {
        return fumacaAlerta;
    }

    public void setFumacaAlerta(double fumacaAlerta) {
        this.fumacaAlerta = fumacaAlerta;
    }

    public double getFumacaCritica() {
        return fumacaCritica;
    }

    public void setFumacaCritica(double fumacaCritica) {
        this.fumacaCritica = fumacaCritica;
    }
}
