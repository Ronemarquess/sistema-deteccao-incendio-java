package br.com.estacio.incendio.dto;

import br.com.estacio.incendio.model.CanalNotificacao;

import java.time.LocalDateTime;

public record NotificacaoDto(
        Long id,
        CanalNotificacao canal,
        LocalDateTime enviadaEm,
        String mensagem,
        String destinatario
) {
}
