package com.devlapa.o_pai_o.domain.estoque;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EstoqueResponseDTO(
        Long id,
        Long produtoId,
        String nomeProduto,
        BigDecimal preco,
        String unidade,
        String categoria,
        String fornecedor,
        Integer quantidade,
        Integer minimo,
        StatusEstoque status,
        LocalDateTime dataUltimaMovimentacao
) {

    public EstoqueResponseDTO(Estoque estoque) {
        this(
                estoque.getId(),
                estoque.getProduto().getId(),
                estoque.getProduto().getNome(),
                estoque.getProduto().getPreco(),
                estoque.getProduto().getUnidade(),
                estoque.getProduto().getCategoria() != null
                        ? estoque.getProduto().getCategoria().getNome()
                        : null,
                estoque.getProduto().getFornecedor() != null
                        ? estoque.getProduto().getFornecedor().getNome()
                        : null,
                estoque.getQuantidade(),
                estoque.getMinimo(),
                estoque.getStatus(),
                estoque.getDataModificacao() != null
                        ? estoque.getDataModificacao()
                        : estoque.getDataCadastro()
        );
    }
}