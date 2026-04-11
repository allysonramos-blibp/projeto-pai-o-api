package com.devlapa.o_pai_o.domain.produtos;

import java.math.BigDecimal;

public record ProdutoUpdateDTO(
        String nome,
        BigDecimal preco,
        String unidade,
        Long categoriaId,
        Long fornecedorId,
        Integer estoque_atual,
        Integer estoque_minimo
) { }
