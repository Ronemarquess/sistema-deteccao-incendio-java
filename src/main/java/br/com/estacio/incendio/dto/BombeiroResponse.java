package br.com.estacio.incendio.dto;

import br.com.estacio.incendio.model.Bombeiro;

public record BombeiroResponse(
        Long id,
        String nome,
        String email,
        String matricula,
        String patente,
        String batalhao,
        String telefone
) {
    public static BombeiroResponse de(Bombeiro bombeiro) {
        return new BombeiroResponse(
                bombeiro.getId(), bombeiro.getNome(), bombeiro.getEmail(),
                bombeiro.getMatricula(), bombeiro.getPatente(),
                bombeiro.getBatalhao(), bombeiro.getTelefone());
    }
}
