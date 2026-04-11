package com.devlapa.o_pai_o.controllers;

import com.devlapa.o_pai_o.domain.comandas.*;
import com.devlapa.o_pai_o.repositories.ComandaRepository;
import com.devlapa.o_pai_o.service.ComandaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/comandas")
public class ComandasController {

    @Autowired
    private ComandaService comandaService;

    @Autowired
    private ComandaRepository repository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM')")
    public ResponseEntity<List<ComandaResponseDTO>> listar() {
        var lista = repository.findAll().stream().map(ComandaResponseDTO::new).toList();
        return ResponseEntity.ok(lista);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM')")
    public ResponseEntity<ComandaResponseDTO> abrir(@RequestBody @Valid ComandaRequestDTO dados) {
        var comanda = comandaService.abrirNovaComanda(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ComandaResponseDTO(comanda));
    }

    @PostMapping("/{id}/itens")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM')")

    public ResponseEntity<ItemResponseDTO> adicionarItem(
            @PathVariable Long id,
            @RequestBody @Valid ItemRequestDTO dados) {

        var item = comandaService.adicionarItem(id, dados.produtoId(), dados.quantidade());
        return ResponseEntity.ok(new ItemResponseDTO(item));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cancelar(@PathVariable Long id) {
        comandaService.cancelarComanda(id);
        return ResponseEntity.ok("Comanda " + id + " cancelada com sucesso e estoque estornado.");
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Comanda> finalizar(@PathVariable Long id) {
        Comanda comandaFinalizada = comandaService.finalizarPagamento(id);
        return ResponseEntity.ok(comandaFinalizada);
    }
}