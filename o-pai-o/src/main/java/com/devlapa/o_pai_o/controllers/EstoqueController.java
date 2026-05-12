package com.devlapa.o_pai_o.controllers;

import com.devlapa.o_pai_o.domain.estoque.EstoquePatchDTO;
import com.devlapa.o_pai_o.domain.estoque.EstoqueRequestDTO;
import com.devlapa.o_pai_o.domain.estoque.EstoqueResponseDTO;
import com.devlapa.o_pai_o.repositories.EstoqueRepository;
import com.devlapa.o_pai_o.service.EstoqueService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueService service;

    @Autowired
    private EstoqueRepository repository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'USUARIO')")
    public ResponseEntity<List<EstoqueResponseDTO>> listar() {
        var lista = service.listarTodos();
        return ResponseEntity.ok(lista);
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> cadastrar(@RequestBody @Valid EstoqueRequestDTO dados) {
        service.cadastrar(dados);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping ("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GERENTE')")
    public ResponseEntity<EstoqueResponseDTO> registrarEntrada(
            @PathVariable Long id,
            @RequestBody @Valid EstoquePatchDTO dto) {

        service.registrarEntrada(id, dto.quantidade());

        var estoque = repository.getReferenceById(id);
        return ResponseEntity.ok(new EstoqueResponseDTO(estoque));
    }

    @PutMapping("/produto/{produtoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Transactional
    public ResponseEntity<Void> atualizarPeloProduto(
            @PathVariable Long produtoId,
            @RequestBody @Valid EstoqueRequestDTO dados) {


        service.atualizarPeloProduto(produtoId, dados.quantidade(), dados.minimo());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/baixo/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'USUARIO')")
    public ResponseEntity<Map<String, Object>> getEstoqueBaixoCount() {
        long count = repository.findAll().stream()
                .filter(e -> e.getQuantidade() <= e.getMinimo())
                .count();

        return ResponseEntity.ok(Map.of("total", count));
    }
}