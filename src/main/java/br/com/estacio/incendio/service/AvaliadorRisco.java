package br.com.estacio.incendio.service;

import br.com.estacio.incendio.config.Limites;
import br.com.estacio.incendio.model.NivelFumaca;
import br.com.estacio.incendio.model.NivelRisco;
import org.springframework.stereotype.Component;

@Component
public class AvaliadorRisco {

    private final Limites limites;

    public AvaliadorRisco(Limites limites) {
        this.limites = limites;
    }

    public NivelRisco avaliar(Double temperatura, Double nivelFumaca) {
        double temp = temperatura == null ? 0 : temperatura;
        double fumaca = nivelFumaca == null ? 0 : nivelFumaca;

        boolean alertaCombinado = temp >= limites.getTemperaturaAlerta()
                && fumaca >= limites.getFumacaAlerta();
        boolean critico = temp >= limites.getTemperaturaCritica()
                || fumaca >= limites.getFumacaCritica();
        if (alertaCombinado || critico) {
            return NivelRisco.ALERTA;
        }
        if (temp >= limites.getTemperaturaAtencao() || fumaca >= limites.getFumacaAtencao()) {
            return NivelRisco.ATENCAO;
        }
        return NivelRisco.NORMAL;
    }

    public NivelFumaca rotuloFumaca(Double nivelFumaca) {
        double fumaca = nivelFumaca == null ? 0 : nivelFumaca;
        if (fumaca >= limites.getFumacaAlerta()) {
            return NivelFumaca.ALTA;
        }
        if (fumaca >= limites.getFumacaAtencao()) {
            return NivelFumaca.MEDIA;
        }
        return NivelFumaca.BAIXA;
    }
}
