package com.devlapa.o_pai_o.domain.comandas;

import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentos;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comandas")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Comanda {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numeroMesa;
    private String nomeCliente;

    @Enumerated(EnumType.STRING)
    private StatusComanda status = StatusComanda.ABERTA;

    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "data_abertura", updatable = false)
    private LocalDateTime dataAbertura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forma_pagamento_id")
    private FormasPagamentos formaPagamento;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL)
    private List<ItemComanda> itens = new ArrayList<>();

    @PrePersist
    public void prePersist(){
        if (this.dataAbertura == null) {
            this.dataAbertura = LocalDateTime.now();
        }
    }
}