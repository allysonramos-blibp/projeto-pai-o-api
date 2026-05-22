package com.devlapa.o_pai_o.domain.usuarios;

public record RedefinirSenhaDTO(
        String token,
        String novaSenha
) {
}