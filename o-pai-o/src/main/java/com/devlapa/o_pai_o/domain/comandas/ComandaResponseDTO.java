package com.devlapa.o_pai_o.domain.comandas;

import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ComandaResponseDTO(
        Long id,
        Integer numeroMesa,
        String nomeCliente,
        String status,
        BigDecimal valorTotal,
        LocalDateTime dataAbertura,
        List<ItemResponseDTO> itens
) {
    public ComandaResponseDTO(Comanda comanda) {
        this(
                comanda.getId(),
                comanda.getNumeroMesa(),
                comanda.getNomeCliente(),
                comanda.getStatus().toString(),
                comanda.getValorTotal(),
                comanda.getDataAbertura(),
                comanda.getItens() != null ?
                        comanda.getItens().stream().map(ItemResponseDTO::new).toList() :
                        List.of()
        );
    }
}