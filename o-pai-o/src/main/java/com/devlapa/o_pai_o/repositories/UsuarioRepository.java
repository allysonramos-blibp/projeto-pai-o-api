package com.devlapa.o_pai_o.repositories;

import com.devlapa.o_pai_o.domain.usuarios.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // 👈 Adicionada a importação do List
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {

    Optional<Usuarios> findByLogin(String login);

    boolean existsByLogin(String login);

    List<Usuarios> findByAtivoTrue();
}