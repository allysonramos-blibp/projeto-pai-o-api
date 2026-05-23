package com.devlapa.o_pai_o.service;


import com.devlapa.o_pai_o.domain.notificacao.Notificacao;
import com.devlapa.o_pai_o.repositories.NotificacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository repository;

    public List<Notificacao> listarAtivas() {
        return repository.findByLidaFalseOrderByDataCriacaoDesc();
    }

    @Transactional
    public void criarAlertaEstoque(String nomeProduto, int quantidade, int minimo) {
        String titulo = "🚨 Estoque Baixo!";
        String mensagem = String.format("O produto '%s' atingiu o nível crítico. Qtd Atual: %d | Mínimo: %d",
                nomeProduto, quantidade, minimo);

        Notificacao notificacao = new Notificacao(titulo, mensagem);
        repository.save(notificacao);
    }

    @Transactional
    public void marcarComoLida(Long id) {
        Notificacao notificacao = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada"));
        notificacao.setLida(true);
        repository.save(notificacao);
    }
}