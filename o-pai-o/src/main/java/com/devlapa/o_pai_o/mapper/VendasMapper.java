package com.devlapa.o_pai_o.mapper;

import com.devlapa.o_pai_o.domain.vendas.Vendas;
import com.devlapa.o_pai_o.domain.vendas.VendasResponseDTO;

public class VendasMapper {

    public static VendasResponseDTO toDTO(Vendas venda) {
        return new VendasResponseDTO(
                venda.getId(),
                venda.getFormasPagamentos(),
                venda.getItens(),
                venda.getValor_total(),
                venda.getStatus(),
                venda.getData_criacao(),
                UsuarioMapper.toDTO(venda.getUsuarioCriacao())
        );
    }
}