package com.devlapa.o_pai_o.domain.vendas;

import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentos;
import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVenda;
import com.devlapa.o_pai_o.domain.usuarios.UsuarioResumoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendasResponseDTO(
        Long id,
        FormasPagamentos formasPagamento,
        List<ItensDeVenda> itens,
        BigDecimal valor_total,
        StatusVenda status,
        LocalDateTime data_criacao,
        UsuarioResumoDTO usuario
) {
}