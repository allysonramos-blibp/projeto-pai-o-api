package com.devlapa.o_pai_o.domain.vendas;

import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentos;
import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVenda;
import com.devlapa.o_pai_o.domain.usuarios.Usuarios;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vendas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "formadepagamento_id", nullable = false)
    private FormasPagamentos formasPagamentos;

    @Column(nullable = false)
    private BigDecimal valor_total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private StatusVenda status = StatusVenda.ABERTA;

    private LocalDateTime data_criacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuarioCriacao;

    @PrePersist
    public void PrePersist() {
        this.data_criacao = LocalDateTime.now();
    }

    // ✅ CORRIGIDO: fetch alterado para EAGER para garantir que os itens
    // sejam sempre carregados junto com a venda, evitando LazyInitializationException
    // Alternativa mais leve: manter LAZY e garantir @Transactional nos services (já feito)
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ItensDeVenda> itens = new ArrayList<>();
}