package com.devlapa.o_pai_o.domain.usuarios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tokens_recuperacao")
public class TokenRecuperacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @Column(nullable = false)
    private LocalDateTime dataExpiracao;

    public TokenRecuperacao(Usuarios usuario) {
        this.usuario = usuario;
        this.token = UUID.randomUUID().toString();
        this.dataExpiracao = LocalDateTime.now().plusHours(2);
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public Usuarios getUsuario() { return usuario; }
    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
    public boolean isExpirado() { return LocalDateTime.now().isAfter(dataExpiracao); }
}