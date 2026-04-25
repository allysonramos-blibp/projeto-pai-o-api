package com.devlapa.o_pai_o.domain.vendas;

import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVendasRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public record VendasRequestDTO(
        Long formasPagamentosId,
        BigDecimal valor_total,
        StatusVenda statusVenda,
        List<ItensDeVendasRequestDTO> itens
) {
}