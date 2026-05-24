package com.devlapa.o_pai_o.repositories;

import com.devlapa.o_pai_o.domain.comandas.Comanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface ComandaRepository extends JpaRepository<Comanda, Long> {

    @Query("SELECT DISTINCT c FROM Comanda c LEFT JOIN FETCH c.itens")
    List<Comanda> findAllComItens();

    @Query("SELECT c FROM Comanda c LEFT JOIN FETCH c.itens WHERE c.id = :id")
    Optional<Comanda> findByIdComItens(@Param("id") Long id);
}