package br.com.estacio.incendio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BombeiroRequestDTO(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String senha,
        @NotBlank String matricula,
        @NotBlank String patente,
        @NotBlank String batalhao,
        @NotBlank String telefone
) {
}
