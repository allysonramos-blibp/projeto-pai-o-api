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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
                .orElseThrow(()-> new RuntimeException("Forma de pagamento não encontrada"));


        Vendas newVenda = new Vendas();
        newVenda.setFormasPagamentos(formasPagamentos);
        newVenda.setUsuarioCriacao(usuarios);
        newVenda.setValor_total(body.valor_total());
        newVenda.setStatus(StatusVenda.ABERTA);
        newVenda.PrePersist();


        if (body.itens() != null && !body.itens().isEmpty()) {
            List<ItensDeVenda> listaItens = body.itens().stream().map(itemDTO -> {
                ItensDeVenda item = new ItensDeVenda();

                Produtos produto = produtosRepository.findById(itemDTO.produtoId())
                                .orElseThrow(() -> new RuntimeException("Produto não encontrado ID: " + itemDTO.produtoId()));
                item.setProduto(produto);

                item.setQuantidade(itemDTO.quantidade());
                item.setPrecoUnitario(itemDTO.precoUnitario());

                item.setPrecoTotal(itemDTO.precoUnitario().multiply(BigDecimal.valueOf(itemDTO.quantidade())));


                item.setVenda(newVenda);

                return item;
            }).toList();

            newVenda.setItens(listaItens);
        }


        Vendas vendasSalva = vendasRepository.save(newVenda);

        return VendasMapper.toDTO(vendasSalva);
    }

    public List<VendasResponseDTO> getVendas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vendas> vendasPage =this.vendasRepository.findAll(pageable);
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

    public VendasResponseDTO getVendaById(Long id) {
        Vendas vendas = vendasRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Venda não encontrada!"));
        try{
            return VendasMapper.toDTO(vendas);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public VendasResponseDTO finalizarVenda(Long id, VendasRequestDTO body) {
        Vendas vendas = vendasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada!"));
        if(vendas.getItens().isEmpty()){
            throw new RuntimeException("Venda não contém itens");
        }
        if(body.formasPagamentosId() != null){
            FormasPagamentos form = formasPagamentosRopository.findById(body.formasPagamentosId())
                    .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada"));
            vendas.setFormasPagamentos(form);
        }
        if(body.valor_total() != null){
            vendas.setValor_total(body.valor_total());
        }
        if(body.statusVenda() != null){
            vendas.setStatus(body.statusVenda());
        }
        for (ItensDeVenda itens : vendas.getItens()) {
            Estoque estoque = estoqueRepository
                    .findByProdutoId(itens.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Estoque não encontrado para o produto: " + itens.getProduto().getNome()
                    ));

            if (estoque.getQuantidade().compareTo(itens.getQuantidade()) < 0) {
                throw new RuntimeException(
                        "Estoque insuficiente para " + itens.getProduto().getNome()
                );
            }

            estoque.setQuantidade(
                    estoque.getQuantidade() - itens.getQuantidade()
            );

            estoque.setDataModificacao(LocalDateTime.now());
        }
        vendas.setStatus(StatusVenda.PAGA);
        vendasRepository.save(vendas);
        return VendasMapper.toDTO(vendas);
    }

    public void cancelarVendas(Long id) {
        Vendas vendas = vendasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venda não encontrada!"));
        if(vendas.getStatus() == StatusVenda.PAGA){
            for(ItensDeVenda itens : vendas.getItens()){
                Estoque estoque = estoqueRepository.findByProdutoId(itens.getProduto().getId())
                        .orElseThrow(() -> new RuntimeException(""));
                estoque.setQuantidade(
                        estoque.getQuantidade() + itens.getQuantidade()
                );
                estoqueRepository.save(estoque);
            }
        }
        vendas.setStatus(StatusVenda.CANCELADA);
        vendasRepository.save(vendas);
    }
}
