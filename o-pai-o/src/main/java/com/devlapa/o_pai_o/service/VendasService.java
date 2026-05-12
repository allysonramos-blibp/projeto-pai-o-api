package com.devlapa.o_pai_o.service;

import com.devlapa.o_pai_o.domain.estoque.Estoque;
import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentos;
import com.devlapa.o_pai_o.domain.itensVenda.ItensDeVenda;
import com.devlapa.o_pai_o.domain.produtos.Produtos;
import com.devlapa.o_pai_o.domain.usuarios.Usuarios;
import com.devlapa.o_pai_o.domain.vendas.StatusVenda;
import com.devlapa.o_pai_o.domain.vendas.Vendas;
import com.devlapa.o_pai_o.domain.vendas.VendasRequestDTO;
import com.devlapa.o_pai_o.domain.vendas.VendasResponseDTO;
import com.devlapa.o_pai_o.mapper.UsuarioMapper;
import com.devlapa.o_pai_o.mapper.VendasMapper;
import com.devlapa.o_pai_o.repositories.EstoqueRepository;
import com.devlapa.o_pai_o.repositories.FormasPagamentosRopository;
import com.devlapa.o_pai_o.repositories.ProdutosRepository;
import com.devlapa.o_pai_o.repositories.VendasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VendasService {

    @Autowired
    VendasRepository vendasRepository;

    @Autowired
    FormasPagamentosRopository formasPagamentosRopository;

    @Autowired
    ProdutosRepository produtosRepository;

    @Autowired
    EstoqueRepository estoqueRepository;

    @Transactional
    public VendasResponseDTO createVenda(VendasRequestDTO body, Usuarios usuarios) {
        FormasPagamentos formasPagamentos = formasPagamentosRopository.findById(body.formasPagamentosId())
                .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada"));

        Vendas newVenda = new Vendas();
        newVenda.setFormasPagamentos(formasPagamentos);
        newVenda.setUsuarioCriacao(usuarios);
        newVenda.setStatus(StatusVenda.ABERTA);
        newVenda.PrePersist();

        if (body.itens() != null && !body.itens().isEmpty()) {
            List<ItensDeVenda> listaItens = body.itens().stream().map(itemDTO -> {
                Produtos produto = produtosRepository.findById(itemDTO.produtoId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado ID: " + itemDTO.produtoId()));

                ItensDeVenda item = new ItensDeVenda();
                item.setProduto(produto);
                item.setQuantidade(itemDTO.quantidade());
                item.setPrecoUnitario(produto.getPreco());
                item.setPrecoTotal(produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.quantidade())));
                item.setVenda(newVenda);
                return item;
            }).toList();

            newVenda.setItens(listaItens);

            BigDecimal total = listaItens.stream()
                    .map(ItensDeVenda::getPrecoTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            newVenda.setValor_total(total);
        } else {
            newVenda.setValor_total(BigDecimal.ZERO);
        }

        Vendas vendasSalva = vendasRepository.save(newVenda);
        return VendasMapper.toDTO(vendasSalva);
    }


    @Transactional(readOnly = true)
    public List<VendasResponseDTO> getVendas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());
        Page<Vendas> vendasPage = this.vendasRepository.findAll(pageable);
        return vendasPage.map(event -> new VendasResponseDTO(
                event.getId(),
                event.getFormasPagamentos(),
                event.getItens(),
                event.getValor_total(),
                event.getStatus(),
                event.getData_criacao(),
                UsuarioMapper.toDTO(event.getUsuarioCriacao())
        )).toList();
    }

    @Transactional(readOnly = true)
    public VendasResponseDTO getVendaById(Long id) {
        Vendas vendas = vendasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada!"));
        try {
            return VendasMapper.toDTO(vendas);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public VendasResponseDTO finalizarVenda(Long id, VendasRequestDTO body) {
        Vendas vendas = vendasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada!"));

        if (vendas.getItens().isEmpty()) {
            throw new RuntimeException("Venda não contém itens");
        }

        if (body.formasPagamentosId() != null) {
            FormasPagamentos form = formasPagamentosRopository.findById(body.formasPagamentosId())
                    .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada"));
            vendas.setFormasPagamentos(form);
        }

        for (ItensDeVenda itens : vendas.getItens()) {
            Estoque estoque = estoqueRepository
                    .findByProdutoId(itens.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Estoque não encontrado para o produto: " + itens.getProduto().getNome()
                    ));

            estoque.baixarEstoque(itens.getQuantidade());

            estoque.setDataModificacao(LocalDateTime.now());

            estoqueRepository.save(estoque);
        }

        vendas.setStatus(StatusVenda.PAGA);
        vendasRepository.save(vendas);
        return VendasMapper.toDTO(vendas);
    }

    @Transactional
    public void cancelarVendas(Long id) {
        Vendas vendas = vendasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada!"));

        if (vendas.getStatus() == StatusVenda.PAGA) {
            for (ItensDeVenda itens : vendas.getItens()) {
                Estoque estoque = estoqueRepository.findByProdutoId(itens.getProduto().getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Estoque não encontrado para o produto: " + itens.getProduto().getNome()
                        ));
                estoque.setQuantidade(estoque.getQuantidade() + itens.getQuantidade());
                estoqueRepository.save(estoque);
            }
        }

        vendas.setStatus(StatusVenda.CANCELADA);
        vendasRepository.save(vendas);
    }

    public BigDecimal getTotalHoje() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDia = inicioDia.plusDays(1);
        BigDecimal total = vendasRepository.somarTotalPorPeriodo(inicioDia, fimDia, StatusVenda.PAGA);
        return (total == null) ? BigDecimal.ZERO : total;
    }

    @Transactional
    public  Vendas atualizarFormaPagamento(Long id, Long novaFormaId) {
        Vendas venda = vendasRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada"));

        if (venda.getStatus() != StatusVenda.ABERTA) {
            throw new RuntimeException("Não é possivel alterar a forma de pagamento de uma venda finalizada");
        }

        FormasPagamentos novaForma = formasPagamentosRopository.findById(novaFormaId)
                .orElseThrow(()-> new RuntimeException("Forma de pagamento não encontrada"));

        venda.setFormasPagamentos(novaForma);
        return vendasRepository.save(venda);
    }

}