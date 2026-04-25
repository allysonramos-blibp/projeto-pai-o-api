package com.devlapa.o_pai_o.service;

import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentoResponseDTO;
import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentos;
import com.devlapa.o_pai_o.domain.formasPagamentos.FormasPagamentosRequestDTO;
import com.devlapa.o_pai_o.repositories.FormasPagamentosRopository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class FormasPagamentoService {

    @Autowired
    FormasPagamentosRopository formasPagamentosRopository;
    @Autowired
    GeradordeIdServices geradordeIdServices;

    public FormasPagamentos createFormaPagament(FormasPagamentosRequestDTO body){
        FormasPagamentos newFormaPagamento = new FormasPagamentos();
        newFormaPagamento.setId(geradordeIdServices.geradorDeId(formasPagamentosRopository));
        newFormaPagamento.setNome(body.nome());
        newFormaPagamento.setDescricao(body.descricao());
        newFormaPagamento.setTipo(body.tipo());
        newFormaPagamento.PrePersist();
        formasPagamentosRopository.save(newFormaPagamento);
        return newFormaPagamento;
    }

    public void deleteFormaDePagamento(Long id) {
        FormasPagamentos formasPagamentos = formasPagamentosRopository.findById(id)
                .orElseThrow(()-> new RuntimeException("Forma de pagamento não encontrada."));
        formasPagamentosRopository.delete(formasPagamentos);
    }

    public List<FormasPagamentoResponseDTO> getFormasDePagamento(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FormasPagamentos> formasPagamentosPage = this.formasPagamentosRopository.findAll(pageable);
        return formasPagamentosPage.map(event -> new FormasPagamentoResponseDTO(
                event.getId(),
                event.getNome(),
                event.getDescricao(),
                event.getTipo(),
                event.isPermitirParcelamento(),
                event.isAtivo(),
                event.getData_criacao(),
                event.getData_modificacao()
        )).stream().toList();

    }

    public FormasPagamentos updareFormasDePagamento(Long id, Map<String, Object> fields) {
        FormasPagamentos updadteFormapagamento = formasPagamentosRopository.findById(id)
                .orElseThrow(()->new RuntimeException("Forma de pagamento não encontrada."));
        fields.forEach((k,v)->{
            Field field = ReflectionUtils.findField(FormasPagamentos.class, k);
            field.setAccessible(true);
            ReflectionUtils.setField(field,updadteFormapagamento,v);

        });
        return formasPagamentosRopository.save(updadteFormapagamento);
    }
}
