    package com.devlapa.o_pai_o.domain.vendas;

    import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentos;
    import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVenda;
    import com.devlapa.o_pai_o.domain.usuarios.UsuarioResumoDTO;
    import com.devlapa.o_pai_o.domain.usuarios.Usuarios;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.util.List;

    public record VendasResponseDTO(Long id, FormasPagamentos formasPagamento, List<ItensDeVenda> itensDeVenda, BigDecimal valor_total, StatusVenda statusVenda, LocalDateTime data_criacao, UsuarioResumoDTO usuarioCriacao) {
    }
