package com.devlapa.o_pai_o.domain.itensVenda;

import com.devlapa.o_pai_o.domain.produtos.Produtos;
import com.devlapa.o_pai_o.domain.vendas.Vendas;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_vendas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItensDeVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendas_id")
    @JsonBackReference
    private Vendas venda;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produtos produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, name = "preco_unitario")
    private BigDecimal precoUnitario = BigDecimal.ZERO;

    @Column(nullable = false, name = "sub_total")
    private BigDecimal precoTotal = BigDecimal.ZERO;

    @PrePersist
    @PreUpdate
    public void calcularPrecoTotal() {
        if (precoUnitario != null && quantidade != null && quantidade > 0) {
            this.precoTotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        }
    }
}