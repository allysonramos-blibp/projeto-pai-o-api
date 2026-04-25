package com.devlapa.o_pai_o.service;

import com.devlapa.o_pai_o.domain.estoque.*;
import com.devlapa.o_pai_o.repositories.EstoqueRepository;
import com.devlapa.o_pai_o.repositories.ProdutosRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository repository;

    @Autowired
    private ProdutosRepository produtosRepository;

    @Transactional
    public void cadastrar(EstoqueRequestDTO dados){
        var produto = produtosRepository.findById(dados.produtoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        var estoque = new Estoque(
                null,
                produto,
                dados.quantidade(),
                dados.minimo(),
                StatusEstoque.NORMAL,
                LocalDateTime.now(),
                null
        );

        estoque.verificarStatus();
        repository.save(estoque);
    }

    public List<EstoqueResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(EstoqueResponseDTO::new)
                .toList();
    }

    @Transactional
    public void registrarEntrada(Long id, Integer quantidadeAdicional) {

        var estoque = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estoque não encontrado"));

        estoque.setQuantidade(estoque.getQuantidade() + quantidadeAdicional);
        estoque.verificarStatus();

        repository.save(estoque);
    }

    @Transactional
    public void atualizarPeloProduto(Long produtoId, Integer novaQuantidade, Integer novoMinimo) {

        var estoque = repository.findByProdutoId(produtoId)
                .orElseGet(() -> {
                    var e = new Estoque();
                    e.setProduto(produtosRepository.getReferenceById(produtoId));
                    e.setDataCadastro(LocalDateTime.now());
                    return e;
                });

        estoque.setQuantidade(novaQuantidade);
        estoque.setMinimo(novoMinimo);
        estoque.setDataModificacao(LocalDateTime.now());

        estoque.verificarStatus();
        repository.save(estoque);
    }
    @Transactional
    public void excluir(Long id) {
        var estoque = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Registro de estoque não encontrado"));
        repository.delete(estoque);
    }
}