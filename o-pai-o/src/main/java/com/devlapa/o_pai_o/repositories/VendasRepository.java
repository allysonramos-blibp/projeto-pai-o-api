package com.devlapa.o_pai_o.repositories;

import com.devlapa.o_pai_o.domain.vendas.StatusVenda;
import com.devlapa.o_pai_o.domain.vendas.Vendas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VendasRepository extends JpaRepository<Vendas, Long> {

    List<Vendas> findAllByOrderByIdDesc();

    @Query("SELECT COALESCE(SUM(v.valor_total), 0) FROM Vendas v WHERE v.data_criacao >= :inicio AND v.data_criacao < :fim AND v.status = :status")
    BigDecimal somarTotalPorPeriodo(LocalDateTime inicio, LocalDateTime fim, StatusVenda status);
}