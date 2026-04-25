package com.devlapa.o_pai_o.domain.categorias;

import com.devlapa.o_pai_o.domain.produtos.Produtos;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Categorias {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    private LocalDateTime data_criacao;

    @PrePersist
    public void PrePersist(){
        data_criacao = LocalDateTime.now();
    }

    @JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<Produtos> produtos;
}
