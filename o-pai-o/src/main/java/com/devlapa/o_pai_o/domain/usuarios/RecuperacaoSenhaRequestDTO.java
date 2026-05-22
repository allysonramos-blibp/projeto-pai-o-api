package com.devlapa.o_pai_o.domain.usuarios;

public record RecuperacaoSenhaRequestDTO(
        String login,
        String email
) {
}