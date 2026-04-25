package com.devlapa.o_pai_o.domain.produtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutosResponseDTO(
        Long id,
        String nome,
        BigDecimal preco,
        String unidade,
        Long categoriaId,
        String nomeCategoria,
        Long fornecedorId,
        Boolean ativo,
        LocalDateTime datacricao,
        Integer estoque_atual,
        Integer estoque_minimo
) {
    public ProdutosResponseDTO(Produtos produto) {
        this(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getUnidade(),
                produto.getCategoria() != null ? produto.getCategoria().getId() : null,
                produto.getCategoria() != null ? produto.getCategoria().getNome() : "Sem Categoria",
                produto.getFornecedor() != null ? produto.getFornecedor().getId() : null,
                produto.getAtivo(),
                produto.getDataCriacao(),
                produto.getEstoque() != null ? produto.getEstoque().getQuantidade() : 0,
                produto.getEstoque() != null ? produto.getEstoque().getMinimo() : 0
        );
    }
}