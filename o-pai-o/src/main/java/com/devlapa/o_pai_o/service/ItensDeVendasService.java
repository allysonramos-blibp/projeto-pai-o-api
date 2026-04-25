package com.devlapa.o_pai_o.service;

import com.devlapa.o_pai_o.domain.estoque.Estoque;
import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVenda;
import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVendasRequestDTO;
import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVendasResponseDTO;
import com.devlapa.o_pai_o.domain.produtos.Produtos;
import com.devlapa.o_pai_o.domain.vendas.Vendas;
import com.devlapa.o_pai_o.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class ItensDeVendasService {

    @Autowired
    ItensDeVendasRepository itensDeVendasRepository;

    @Autowired
    VendasRepository vendasRepository;

    @Autowired
    ProdutosRepository produtosRepository;

    @Autowired
    EstoqueRepository estoqueRepository;

    @Transactional
    public ItensDeVendasResponseDTO postItens(ItensDeVendasRequestDTO body) {

        if (body.quantidade() <= 0) {
            throw new RuntimeException("Quantidade inválida");
        }

        Vendas venda = vendasRepository.findById(body.vendaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada"));

        Produtos produto = produtosRepository.findById(body.produtoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        if (!produto.getAtivo()) {
            throw new RuntimeException("Produto inativo");
        }

        Estoque estoque = estoqueRepository.findByProdutoId(body.produtoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado"));

        // ✅ CORRIGIDO: removida verificação duplicada com bug (| ao invés de ||)
        // Mantida apenas a verificação correta com BigDecimal.compareTo
        if (estoque.getQuantidade().compareTo(body.quantidade()) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente para o produto: " + produto.getNome());
        }

        ItensDeVenda itensDeVenda = new ItensDeVenda();
        itensDeVenda.setProduto(produto);
        itensDeVenda.setVenda(venda);
        itensDeVenda.setQuantidade(body.quantidade());
        itensDeVenda.setPrecoUnitario(produto.getPreco()); // ✅ sempre usa o preço real do produto
        itensDeVenda.calcularPrecoTotal();

        // Atualiza o valor total da venda
        venda.setValor_total(
                (venda.getValor_total() != null ? venda.getValor_total() : BigDecimal.ZERO)
                        .add(itensDeVenda.getPrecoTotal())
        );
        vendasRepository.save(venda);

        itensDeVendasRepository.save(itensDeVenda);

        // ✅ CORRIGIDO: desconta do estoque após salvar o item (estava faltando)
        estoque.setQuantidade(estoque.getQuantidade() - body.quantidade());
        if (estoque.getQuantidade() <= 0) {
            estoque.setQuantidade(0);
        }
        estoqueRepository.save(estoque);

        return new ItensDeVendasResponseDTO(
                itensDeVenda.getId(),
                itensDeVenda.getProduto(),
                itensDeVenda.getQuantidade(),
                itensDeVenda.getPrecoUnitario(),
                itensDeVenda.getPrecoTotal()
        );
    }

    public void deleteIten(Long id) {
        ItensDeVenda itensDeVenda = itensDeVendasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        itensDeVendasRepository.delete(itensDeVenda);
    }

    public ItensDeVenda updateItem(Long id, ItensDeVendasRequestDTO body) {
        ItensDeVenda itensDeVenda = itensDeVendasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        Produtos produtos = produtosRepository.findById(body.produtoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (body.produtoId() != null) {
            itensDeVenda.setProduto(produtos);
        }
        if (body.quantidade() != null) {
            itensDeVenda.setQuantidade(body.quantidade());
        }

        itensDeVenda.calcularPrecoTotal();
        return itensDeVendasRepository.save(itensDeVenda);
    }

    public Page<ItensDeVendasResponseDTO> getAllItensVendas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return itensDeVendasRepository.findAll(pageable)
                .map(item -> new ItensDeVendasResponseDTO(
                        item.getId(),
                        item.getProduto(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getPrecoTotal()
                ));
    }
}