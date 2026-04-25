package com.devlapa.o_pai_o.repositories;

import com.devlapa.o_pai_o.domain.estoque.Estoque;
import com.devlapa.o_pai_o.domain.estoque.StatusEstoque;
import com.devlapa.o_pai_o.domain.produtos.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    Optional<Estoque> findByProdutoId(Long produtoId);
    List<Estoque> findByStatus(StatusEstoque status);
    Optional<Estoque> findByProduto(Produtos produtos);
}
