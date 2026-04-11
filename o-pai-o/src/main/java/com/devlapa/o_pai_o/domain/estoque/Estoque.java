package com.devlapa.o_pai_o.domain.estoque;

import com.devlapa.o_pai_o.domain.produtos.Produtos;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "estoque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id")
    private Produtos produto;

    private Integer quantidade;
    private Integer minimo;

    @Enumerated(EnumType.STRING)
    private StatusEstoque status;


    private LocalDateTime dataCadastro;
    private LocalDateTime dataModificacao;

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dataModificacao = LocalDateTime.now();
    }

    public void baixarEstoque(Integer qtdVendida) {
        if (this.quantidade == null) {
            this.quantidade = 0;
        }

        if (this.quantidade < qtdVendida) {
            throw new RuntimeException("Estoque  insuficiente! Atual: " + this.quantidade + "Solicitada: " + qtdVendida);
        }
        this.quantidade -= qtdVendida;

        verificarStatus();
    }

    public void verificarStatus() {
        if (this.quantidade == null || this.quantidade <= 0) {
            this.status = StatusEstoque.ESGOTADO;
        } else if (this.minimo != null && this.quantidade <= this.minimo) {
            this.status = StatusEstoque.BAIXO;
        } else {
            this.status = StatusEstoque.NORMAL;
        }
    }
}