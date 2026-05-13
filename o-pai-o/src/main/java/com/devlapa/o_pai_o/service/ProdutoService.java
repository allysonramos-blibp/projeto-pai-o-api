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

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    ProdutosRepository produtosRepository;

    @Autowired
    CategoriasRepository categoriasRepository;

    @Autowired
    private FornecedoresRepository fornecedoresRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;



    @Autowired
    private GeradordeIdServices geradordeIdServices;


    public Produtos createproduto(ProdutosRequestDTO body) {
        Categorias categorias = (body.categoriaId() != null)
                ? categoriasRepository.findById(body.categoriaId()).orElse(null) : null;
        Fornecedores fornecedores = (body.fornecedoresId() != null)
                ? fornecedoresRepository.findById(body.fornecedoresId()).orElse(null) : null;

        Produtos newProdutos = new Produtos();
        newProdutos.setNome(body.nome());
        newProdutos.setPreco(body.preco());
        newProdutos.setUnidade(body.unidade());
        newProdutos.setFornecedor(fornecedores);
        newProdutos.setCategoria(categorias);
        return produtosRepository.save(newProdutos);
    }

    public List<ProdutosResponseDTO> getProdutos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Produtos> produtosPage = this.produtosRepository.findAll(pageable);

        return produtosPage.map(produto -> {
            Estoque estoque = estoqueRepository.findByProduto(produto).orElse(null);


            return new ProdutosResponseDTO(
                    produto.getId(),
                    produto.getNome(),
                    produto.getPreco(),
                    produto.getUnidade(),
                    produto.getCategoria() != null ? produto.getCategoria().getId() : null,
                    produto.getCategoria() != null ? produto.getCategoria().getNome() : "Sem Categoria",
                    produto.getFornecedor() != null ? produto.getFornecedor().getId() : null,
                    produto.getAtivo(),
                    produto.getDataCriacao(),
                    estoque != null ? estoque.getQuantidade() : 0,
                    estoque != null ? estoque.getMinimo() : 0
            );
        }).toList();
    }

    public void deleteProduto(Long id) {
        Produtos produto = produtosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
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