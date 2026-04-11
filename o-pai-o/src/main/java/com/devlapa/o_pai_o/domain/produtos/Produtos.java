package com.devlapa.o_pai_o.domain.produtos;

import com.devlapa.o_pai_o.domain.categorias.Categorias;
import com.devlapa.o_pai_o.domain.estoque.Estoque;
import com.devlapa.o_pai_o.domain.fornecedores.Fornecedores;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "produtos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produtos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private BigDecimal preco;
    private String unidade;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categorias categoria;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id")
    private Fornecedores fornecedor;

    private Boolean ativo;
    private LocalDateTime dataCriacao;


    @OneToOne(mappedBy = "produto", cascade = CascadeType.ALL)
    private Estoque estoque;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        if (this.ativo == null) this.ativo = true;
    }
}