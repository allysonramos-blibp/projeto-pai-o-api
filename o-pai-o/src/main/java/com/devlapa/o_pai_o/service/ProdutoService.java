package com.devlapa.o_pai_o.service;

import com.devlapa.o_pai_o.domain.categorias.Categorias;
import com.devlapa.o_pai_o.domain.estoque.Estoque;
import com.devlapa.o_pai_o.domain.fornecedores.Fornecedores;
import com.devlapa.o_pai_o.domain.produtos.ProdutoUpdateDTO;
import com.devlapa.o_pai_o.domain.produtos.Produtos;
import com.devlapa.o_pai_o.domain.produtos.ProdutosRequestDTO;
import com.devlapa.o_pai_o.domain.produtos.ProdutosResponseDTO;
import com.devlapa.o_pai_o.repositories.CategoriasRepository;
import com.devlapa.o_pai_o.repositories.EstoqueRepository;
import com.devlapa.o_pai_o.repositories.FornecedoresRepository;
import com.devlapa.o_pai_o.repositories.ProdutosRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ProdutoService {

    @Autowired
    ProdutosRepository produtosRepository;

    @Autowired
    CategoriasRepository categoriasRepository;

    @Autowired
    private FornecedoresRepository fornecedoresRepository;

    @Autowired
    GeradordeIdServices geradordeIdServices;

    @Autowired
    private EstoqueRepository estoqueRepository;


    public Produtos createproduto( ProdutosRequestDTO body) {
        Categorias categorias = categoriasRepository.findById(body.categoriaId())
                .orElseThrow(() -> new RuntimeException ("Categoria não encontrado"));
        Fornecedores fornecedores = fornecedoresRepository.findById(body.fornecedoresId())
                .orElseThrow(() -> new RuntimeException ("Fornecedor não encontrado"));

        Produtos newProdutos = new Produtos();
        newProdutos.setNome(body.nome());
        newProdutos.setPreco(body.preco());
        newProdutos.setUnidade(body.unidade());
        newProdutos.setFornecedor(fornecedores);
        newProdutos.setCategoria(categorias);
        produtosRepository.save(newProdutos);
        return newProdutos;
    }

    public List<ProdutosResponseDTO> getProdutos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Produtos> produtosPage = this.produtosRepository.findAll(pageable);

        return produtosPage.map(event -> {

            Estoque estoque = estoqueRepository.findByProduto(event).orElse(null);

            return new ProdutosResponseDTO(
                    event.getId(),
                    event.getNome(),
                    event.getPreco(),
                    event.getUnidade(),
                    event.getCategoria() != null ? event.getCategoria().getId() : null,
                    event.getFornecedor() != null ? event.getFornecedor().getId() : null,
                    event.getAtivo(),
                    event.getDataCriacao(),
                    estoque != null ? estoque.getQuantidade() : 0,
                    estoque != null ? estoque.getMinimo() : 0
            );
        }).toList();
    }

    public void deleteProduto(Long id) {
        Produtos produto = produtosRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Produto não encontrado"));
        produtosRepository.delete(produto);
    }

    @Transactional
    public Produtos updateProdutos(Long id, ProdutoUpdateDTO fields) {
        Produtos produto = produtosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado."));


        if (fields.nome() != null) produto.setNome(fields.nome());
        if (fields.preco() != null) produto.setPreco(fields.preco());
        if (fields.unidade() != null) produto.setUnidade(fields.unidade());

        if (fields.categoriaId() != null) {
            produto.setCategoria(categoriasRepository.findById(fields.categoriaId()).orElse(null));
        }
        if (fields.fornecedorId() != null) {
            produto.setFornecedor(fornecedoresRepository.findById(fields.fornecedorId()).orElse(null));
        }


        if (fields.estoque_atual() != null || fields.estoque_minimo() != null) {
            Estoque estoque = estoqueRepository.findByProduto(produto)
                    .orElseGet(() -> {
                        Estoque novo = new Estoque();
                        novo.setProduto(produto);
                        return novo;
                    });

            if (fields.estoque_atual() != null) estoque.setQuantidade(fields.estoque_atual());
            if (fields.estoque_minimo() != null) estoque.setMinimo(fields.estoque_minimo());

            estoque.verificarStatus();
            estoqueRepository.save(estoque);
        }

        return produtosRepository.save(produto);
    }
}
