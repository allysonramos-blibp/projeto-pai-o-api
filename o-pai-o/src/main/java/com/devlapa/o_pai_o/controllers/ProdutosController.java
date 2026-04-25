package com.devlapa.o_pai_o.controllers;

import com.devlapa.o_pai_o.domain.produtos.ProdutoUpdateDTO;
import com.devlapa.o_pai_o.domain.produtos.Produtos;
import com.devlapa.o_pai_o.domain.produtos.ProdutosRequestDTO;
import com.devlapa.o_pai_o.domain.produtos.ProdutosResponseDTO;
import com.devlapa.o_pai_o.repositories.ProdutosRepository;
import com.devlapa.o_pai_o.service.EstoqueService;
import com.devlapa.o_pai_o.service.ProdutoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produtos")
public class ProdutosController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutosRepository produtosRepository;


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Produtos> create(@RequestBody @Validated ProdutosRequestDTO body){
        Produtos newProduto = this.produtoService.createproduto(body);
        return ResponseEntity.ok(newProduto);
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'USUARIO')")
    public ResponseEntity<List<ProdutosResponseDTO>> getProdutosList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        List<ProdutosResponseDTO> allProdutos = this.produtoService.getProdutos(page, size);
        return ResponseEntity.ok(allProdutos);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'USUARIO')")
    public ResponseEntity<Produtos> getProdutoId(@PathVariable Long id){
        return produtosRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduto(@PathVariable Long id){
        produtoService.deleteProduto(id);
        return ResponseEntity.ok("Produto deletado com sucesso");
    }



    @Autowired
    private EstoqueService estoqueService;

    @PatchMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Produtos> updateProduto(@PathVariable Long id, @RequestBody @Validated ProdutoUpdateDTO fields){

        Produtos updated = produtoService.updateProdutos(id, fields);
        return ResponseEntity.ok(updated);
    }
}