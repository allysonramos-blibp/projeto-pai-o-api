package com.devlapa.o_pai_o.repositories;

import com.devlapa.o_pai_o.domain.usuarios.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuarios, Long> {
    Optional<Usuarios> findByLogin(String login);
    Optional<Usuarios> findByEmail(String email);
    List<Usuarios> findByAtivoTrue();
    boolean existsByLogin(String login);
}