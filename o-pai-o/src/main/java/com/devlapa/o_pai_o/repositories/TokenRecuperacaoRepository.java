package com.devlapa.o_pai_o.repositories;

import com.devlapa.o_pai_o.domain.usuarios.TokenRecuperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, Long> {
    Optional<TokenRecuperacao> findByToken(String token);
    void deleteByUsuarioId(Long usuarioId);
}