package com.devlapa.o_pai_o.service;

import com.devlapa.o_pai_o.domain.contas.ContaPagar;
import com.devlapa.o_pai_o.domain.contas.DadosCadastroContaPagar;
import com.devlapa.o_pai_o.domain.contas.StatusConta;
import com.devlapa.o_pai_o.domain.fornecedores.Fornecedores;
import com.devlapa.o_pai_o.domain.usuarios.Usuarios;
import com.devlapa.o_pai_o.repositories.ContasPagarRepository;
import com.devlapa.o_pai_o.repositories.FornecedoresRepository;
import com.devlapa.o_pai_o.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ContasPagarService {

    @Autowired
    private ContasPagarRepository repository;

    @Autowired
    private FornecedoresRepository fornecedorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<ContaPagar> listarTodas() {
        return repository.findAll();
    }

    @Transactional
    public ContaPagar salvar(DadosCadastroContaPagar dados) {
        var conta = new ContaPagar();
        conta.setDescricao(dados.descricao());
        conta.setValor(dados.valor());
        conta.setDataVencimento(dados.dataVencimento());
        conta.setCategoria(dados.categoria());
        conta.setFornecedor(buscarFornecedor(dados.fornecedorId()));
        conta.setUserCriacao(buscarUsuario(dados.usuarioId()));
        conta.setStatus(StatusConta.PENDENTE);

        return repository.save(conta);
    }

    @Transactional
    public ContaPagar atualizar(Long id, DadosCadastroContaPagar dados) {
        var conta = buscarConta(id);
        conta.setDescricao(dados.descricao());
        conta.setValor(dados.valor());
        conta.setDataVencimento(dados.dataVencimento());
        conta.setCategoria(dados.categoria());
        conta.setFornecedor(buscarFornecedor(dados.fornecedorId()));
        conta.setUserCriacao(buscarUsuario(dados.usuarioId()));

        return repository.save(conta);
    }

    @Transactional
    public void marcarComoPaga(Long id) {
        var conta = buscarConta(id);


        if (StatusConta.PAGA.equals(conta.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta conta já foi paga.");
        }

        if (StatusConta.CANCELADA.equals(conta.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Conta cancelada não pode ser paga.");
        }

        conta.setStatus(StatusConta.PAGA);
        conta.setDataPagamento(LocalDate.now());
        repository.save(conta);
    }

    public Map<String, BigDecimal> obterResumoFinanceiro() {
        List<ContaPagar> todas = repository.findAll();


        BigDecimal totalPago = todas.stream()
                .filter(c -> StatusConta.PAGA.equals(c.getStatus()))
                .map(ContaPagar::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        
        BigDecimal totalVencido = todas.stream()
                .filter(c -> !StatusConta.PAGA.equals(c.getStatus()) &&
                        c.getDataVencimento() != null &&
                        c.getDataVencimento().isBefore(LocalDate.now()))
                .map(ContaPagar::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of("TotalPago", totalPago, "TotalVencido", totalVencido);
    }

    @Transactional
    public void deletar(Long id) {
        repository.delete(buscarConta(id));
    }

    private ContaPagar buscarConta(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada"));
    }

    private Fornecedores buscarFornecedor(Long fornecedorId) {
        return fornecedorRepository.findById(fornecedorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado"));
    }

    private Usuarios buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }
}