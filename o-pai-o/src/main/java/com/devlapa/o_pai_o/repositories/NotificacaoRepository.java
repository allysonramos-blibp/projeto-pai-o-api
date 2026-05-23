package com.devlapa.o_pai_o.repositories;

import com.devlapa.o_pai_o.domain.notificacao.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByLidaFalseOrderByDataCriacaoDesc();
}