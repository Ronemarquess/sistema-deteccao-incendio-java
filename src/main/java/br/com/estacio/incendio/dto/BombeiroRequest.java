package br.com.estacio.incendio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BombeiroRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String senha,
        String matricula,
        String patente,
        String batalhao,
        String telefone
) {
}
