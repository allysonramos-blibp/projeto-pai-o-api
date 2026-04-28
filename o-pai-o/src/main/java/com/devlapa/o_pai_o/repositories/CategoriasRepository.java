package com.devlapa.o_pai_o.repositories;

import com.devlapa.o_pai_o.domain.categorias.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoriasRepository extends JpaRepository<Categorias, Long> {
    boolean existsByNome(String nome);
}
