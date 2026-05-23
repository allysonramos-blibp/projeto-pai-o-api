package com.devlapa.o_pai_o.controllers;

import com.devlapa.o_pai_o.domain.notificacao.Notificacao;
import com.devlapa.o_pai_o.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService service;

    @GetMapping
    public ResponseEntity<List<Notificacao>> obterNotificacoesAtivas() {
        return ResponseEntity.ok(service.listarAtivas());
    }

    @PatchMapping("/{id}/ler")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        service.marcarComoLida(id);
        return ResponseEntity.noContent().build();
    }
}