package com.devlapa.o_pai_o.domain.comandas;

import java.math.BigDecimal;
import java.util.List;

public record ComandaResponseDTO(
        Long id,
        Integer numeroMesa,
        String nomeCliente,
        String status,
        BigDecimal valorTotal,
        List<ItemResponseDTO> itens // ADICIONE ESTA LINHA
) {
    public ComandaResponseDTO(Comanda comanda) {
        this(
                comanda.getId(),
                comanda.getNumeroMesa(),
                comanda.getNomeCliente(),
                comanda.getStatus().toString(),
                comanda.getValorTotal(),
                comanda.getItens() != null ?
                        comanda.getItens().stream().map(ItemResponseDTO::new).toList() :
                        List.of()
        );
    }
}