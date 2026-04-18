package com.devlapa.o_pai_o.controllers;

import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentoResponseDTO;
import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentos;
import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentosRequestDTO;
import com.devlapa.o_pai_o.repositories.FormasPagamentosRopository;
import com.devlapa.o_pai_o.service.FormasPagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/formasdepagamento")
public class FormasPagamentoController {

    @Autowired
    FormasPagamentoService formasPagamentoService;
    @Autowired
    FormasPagamentosRopository formasPagamentosRopository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<FormasPagamentos> create(@RequestBody FormasPagamentosRequestDTO body) {
        FormasPagamentos newFormaPagament = this.formasPagamentoService.createFormaPagament(body);
        return ResponseEntity.ok(newFormaPagament);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        formasPagamentoService.deleteFormaDePagamento(id);
        return ResponseEntity.ok("Forma de Pagamento deletada!");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'GARCOM')")
    public ResponseEntity<List<FormasPagamentoResponseDTO>> getFormasDePagamento(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<FormasPagamentoResponseDTO> allFormasDePagamento = this.formasPagamentoService.getFormasDePagamento(page, size);
        return ResponseEntity.ok(allFormasDePagamento);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public FormasPagamentos update(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return formasPagamentoService.updareFormasDePagamento(id, fields);
    }

    @GetMapping("/find/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','GARCON')")
    public ResponseEntity<FormasPagamentos> getIdFormaDePagamento(@PathVariable Long id) {
        return formasPagamentosRopository.findById(id)
                .map(ResponseEntity :: ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
