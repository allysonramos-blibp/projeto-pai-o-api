package com.devlapa.o_pai_o.controllers;

import com.devlapa.o_pai_o.domain.contas.ContaPagarResponseDTO; // Verifique se o nome do arquivo é este mesmo
import com.devlapa.o_pai_o.domain.contas.DadosCadastroContaPagar;
import com.devlapa.o_pai_o.service.ContasPagarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contas")
public class ContasPagarController {

    @Autowired
    private ContasPagarService service;

    @GetMapping
    public List<ContaPagarResponseDTO> listar() {
        return service.listarTodas().stream().map(ContaPagarResponseDTO::new).toList();
    }

    @GetMapping("/resumo")
    public ResponseEntity<Map<String, BigDecimal>> obterResumo() {
        return ResponseEntity.ok(service.obterResumoFinanceiro());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ContaPagarResponseDTO> cadastrar(@RequestBody @Valid DadosCadastroContaPagar dados) {
        return ResponseEntity.ok(new ContaPagarResponseDTO(service.salvar(dados)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ContaPagarResponseDTO> editar(@PathVariable Long id, @RequestBody @Valid DadosCadastroContaPagar dados) {

        return ResponseEntity.ok(new ContaPagarResponseDTO(service.atualizar(id, dados)));
    }

    @PatchMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> pagar(@PathVariable Long id) {
        service.marcarComoPaga(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}