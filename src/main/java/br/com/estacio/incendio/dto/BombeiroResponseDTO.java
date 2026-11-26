package br.com.estacio.incendio.dto;

import br.com.estacio.incendio.model.Bombeiro;

public record BombeiroResponseDTO(
        Long id,
        String nome,
        String email,
        Boolean ativo,
        String matricula,
        String patente,
        String batalhao,
        String telefone
) {
    public static BombeiroResponseDTO fromEntity(Bombeiro bombeiro) {
        return new BombeiroResponseDTO(
                bombeiro.getId(),
                bombeiro.getNome(),
                bombeiro.getEmail(),
                bombeiro.getAtivo(),
                bombeiro.getMatricula(),
                bombeiro.getPatente(),
                bombeiro.getBatalhao(),
                bombeiro.getTelefone()
        );
    }
}
