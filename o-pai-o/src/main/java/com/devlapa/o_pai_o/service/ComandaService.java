package com.devlapa.o_pai_o.service;

import com.devlapa.o_pai_o.domain.comandas.Comanda;
import com.devlapa.o_pai_o.domain.comandas.ComandaRequestDTO;
import com.devlapa.o_pai_o.domain.comandas.ItemComanda;
import com.devlapa.o_pai_o.domain.comandas.StatusComanda;
import com.devlapa.o_pai_o.domain.estoque.Estoque;
import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentos;
import com.devlapa.o_pai_o.domain.produtos.Produtos;
import com.devlapa.o_pai_o.repositories.ComandaRepository;
import com.devlapa.o_pai_o.repositories.EstoqueRepository;
import com.devlapa.o_pai_o.repositories.FormasPagamentosRopository;
import com.devlapa.o_pai_o.repositories.ItemComandaRepository;
import com.devlapa.o_pai_o.repositories.ProdutosRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ComandaService {

    @Autowired
    private ComandaRepository comandaRepository;

    @Autowired
    private ProdutosRepository produtosRepository;

    @Autowired
    private ItemComandaRepository itemComandaRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private FormasPagamentosRopository formasPagamentosRopository;

    public Comanda abrirNovaComanda(ComandaRequestDTO dados) {
        Comanda comanda = new Comanda();
        comanda.setNumeroMesa(dados.numeroMesa());
        comanda.setNomeCliente(dados.nomeCliente());
        comanda.setStatus(StatusComanda.ABERTA);
        comanda.setValorTotal(BigDecimal.ZERO);
        return comandaRepository.save(comanda);
    }

    @Transactional
    public ItemComanda adicionarItem(Long comandaId, Long produtoId, Integer qtd) {

        Comanda comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RuntimeException("ERRO: Comanda ID " + comandaId + " não encontrada!"));

        Produtos produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("ERRO: Produto ID " + produtoId + " não encontrado!"));

        Estoque estoque = estoqueRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new RuntimeException("Erro: O item " + produtoId + " solicitado não foi encontrado ou não possui estoque disponível"));

        if (estoque.getQuantidade() == null || estoque.getQuantidade() < qtd) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        ItemComanda item = new ItemComanda();
        item.setComanda(comanda);
        item.setProdutos(produto);
        item.setQuantidade(qtd);

        BigDecimal preco = produto.getPreco();
        if (preco == null) {
            throw new RuntimeException("O produto " + produto.getNome() + " está sem preço cadastrado!");
        }

        item.setPrecoUnitario(preco);
        item.setSubtotal(preco.multiply(BigDecimal.valueOf(qtd)));

        comanda.setValorTotal(comanda.getValorTotal().add(item.getSubtotal()));
        estoque.setQuantidade(estoque.getQuantidade() - qtd);

        comandaRepository.save(comanda);
        estoqueRepository.save(estoque);

        return itemComandaRepository.save(item);
    }

    @Transactional
    public void cancelarComanda(Long id) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ERRO: Comanda não encontrada!"));

        if (comanda.getStatus() != StatusComanda.ABERTA) {
            throw new RuntimeException("Não é possível cancelar uma comanda com status: " + comanda.getStatus());
        }

        if (comanda.getItens() != null) {
            for (ItemComanda item : comanda.getItens()) {
                Produtos produto = item.getProdutos();
                Estoque estoque = estoqueRepository.findByProdutoId(produto.getId())
                        .orElseThrow(() -> new RuntimeException("Erro: O item solicitado não foi encontrado ou não possui estoque disponível"));
                estoque.setQuantidade(estoque.getQuantidade() + item.getQuantidade());
                estoqueRepository.save(estoque);
            }
        }

        comanda.setStatus(StatusComanda.CANCELADA);
        comanda.setValorTotal(BigDecimal.ZERO);

        comandaRepository.save(comanda);
    }

    @Transactional
    public void removerItem(Long itemId) {

        var item = itemComandaRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado"));

        var comanda = item.getComanda();

        if (!comanda.getStatus().equals(StatusComanda.ABERTA)) {
            throw new RuntimeException("Não é possível remover itens de uma comanda fechada.");
        }

        comanda.getItens().remove(item);
        comanda.setValorTotal(comanda.getValorTotal().subtract(item.getSubtotal()));

        itemComandaRepository.delete(item);
        comandaRepository.save(comanda);
    }

    @Transactional
    public Comanda finalizarPagamento(Long id, Long formaPagamentoId) {
        Comanda comanda = comandaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comanda não encontrada"));

        if (comanda.getStatus() != StatusComanda.ABERTA) {
            throw new RuntimeException("Apenas comandas ABERTAS podem ser finalizadas. Status atual: " + comanda.getStatus());
        }

        if (comanda.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Não é possível finalizar uma comanda sem consumo (valor zero).");
        }

        FormasPagamentos forma = formasPagamentosRopository.findById(formaPagamentoId)
                .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada: " + formaPagamentoId));

        if (!forma.isAtivo()) {
            throw new RuntimeException("A forma de pagamento selecionada está inativa.");
        }

        comanda.setFormaPagamento(forma);
        comanda.setStatus(StatusComanda.FINALIZADA);

        return comandaRepository.save(comanda);
    }
}