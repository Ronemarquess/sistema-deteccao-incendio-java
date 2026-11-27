package br.com.estacio.incendio.service;

import br.com.estacio.incendio.config.Limites;
import br.com.estacio.incendio.model.NivelFumaca;
import br.com.estacio.incendio.model.NivelRisco;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvaliadorRiscoTest {

    private final AvaliadorRisco avaliador = new AvaliadorRisco(new Limites());

    @Test
    void limitesDeAtencaoNosLimitesInclusivos() {
        assertEquals(NivelRisco.ATENCAO, avaliador.avaliar(40.0, 5.0));
        assertEquals(NivelRisco.ATENCAO, avaliador.avaliar(26.0, 30.0));
        assertEquals(NivelRisco.NORMAL, avaliador.avaliar(39.9, 29.9));
    }

    @Test
    void regraCombinadaDeAlerta() {
        assertEquals(NivelRisco.ALERTA, avaliador.avaliar(57.0, 50.0));
        assertEquals(NivelRisco.ATENCAO, avaliador.avaliar(57.0, 49.9));
        assertEquals(NivelRisco.ATENCAO, avaliador.avaliar(56.9, 50.0));
    }

    @Test
    void limiteCriticoIsolaTemperaturaOuFumaca() {
        assertEquals(NivelRisco.ALERTA, avaliador.avaliar(80.0, 0.0));
        assertEquals(NivelRisco.ALERTA, avaliador.avaliar(0.0, 85.0));
        assertEquals(NivelRisco.ALERTA, avaliador.avaliar(79.9, 84.9));
        assertEquals(NivelRisco.ATENCAO, avaliador.avaliar(56.0, 84.9));
    }

    @Test
    void rotuloDeFumaca() {
        assertEquals(NivelFumaca.BAIXA, avaliador.rotuloFumaca(29.9));
        assertEquals(NivelFumaca.MEDIA, avaliador.rotuloFumaca(30.0));
        assertEquals(NivelFumaca.MEDIA, avaliador.rotuloFumaca(49.9));
        assertEquals(NivelFumaca.ALTA, avaliador.rotuloFumaca(50.0));
    }
}
