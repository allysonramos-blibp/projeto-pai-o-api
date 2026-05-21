package com.devlapa.o_pai_o.domain.usuarios;

import java.time.LocalDateTime;

public record UsuariosResponseDTO(
        Long id,
        String nome,
        String login,
        String perfil,
        Boolean ativo,
        LocalDateTime dataCadastro
) {
    public UsuariosResponseDTO(Usuarios u) {
        this(u.getId(), u.getNome(), u.getLogin(), u.getPerfil(), u.getAtivo(), u.getDataCadastro());
    }
}