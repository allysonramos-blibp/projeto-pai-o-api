package com.devlapa.o_pai_o.controllers;

import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVenda;
import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVendasRequestDTO;
import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVendasResponseDTO;
import com.devlapa.o_pai_o.service.ItensDeVendasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/itensvenda")
public class ItensDeVendasController {

    @Autowired
    ItensDeVendasService itensDeVendasService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','USUARIO')")
    public ResponseEntity<ItensDeVendasResponseDTO> postIten(@RequestBody ItensDeVendasRequestDTO body) {
        ItensDeVendasResponseDTO itensDeVenda = this.itensDeVendasService.postItens(body);
        return ResponseEntity.ok(itensDeVenda);
    }

    @DeleteMapping("/item/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','USUARIO')")
    public ResponseEntity<String> deleteItens(@PathVariable Long id) {
        itensDeVendasService.deleteIten(id);
        return ResponseEntity.ok("Item deletado com sucesso!");
    }

    @PatchMapping("/item/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','USUARIO')")
    public ResponseEntity<ItensDeVenda> updateItem(@PathVariable Long id, @RequestBody ItensDeVendasRequestDTO body) {
        return ResponseEntity.ok(itensDeVendasService.updateItem(id, body));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','USUARIO')")
    public ResponseEntity<Page<ItensDeVendasResponseDTO>> allItensVendas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(itensDeVendasService.getAllItensVendas(page, size));
    }
}