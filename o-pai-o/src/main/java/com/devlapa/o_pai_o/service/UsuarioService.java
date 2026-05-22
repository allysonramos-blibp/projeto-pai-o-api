package com.devlapa.o_pai_o.service;

import com.devlapa.o_pai_o.domain.usuarios.*;
import com.devlapa.o_pai_o.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacaoRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, TokenRecuperacaoRepository tokenRepository,
                          EmailService emailService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuariosResponseDTO> listarTodos() {
        return usuarioRepository.findByAtivoTrue().stream()
                .map(u -> new UsuariosResponseDTO(
                        u.getId(), u.getNome(), u.getLogin(), u.getEmail(),
                        u.getPerfil(), u.getAtivo(), u.getDataCadastro()
                )).toList();
    }

    @Transactional
    public UsuariosResponseDTO salvar(UsuariosRequestDTO dto) {
        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new RuntimeException("Login já está em uso");
        }
        Usuarios usuario = new Usuarios();
        usuario.setNome(dto.nome());
        usuario.setLogin(dto.login());
        usuario.setEmail(dto.email());
        usuario.setHash(passwordEncoder.encode(dto.senha()));
        usuario.setAtivo(true);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setPerfil(dto.perfil() != null && !dto.perfil().isBlank() ? dto.perfil().toUpperCase() : "USUARIO");

        Usuarios salvo = usuarioRepository.save(usuario);
        return new UsuariosResponseDTO(salvo.getId(), salvo.getNome(), salvo.getLogin(), salvo.getEmail(), salvo.getPerfil(), salvo.getAtivo(), salvo.getDataCadastro());
    }

    @Transactional
    public UsuariosResponseDTO atualizar(Long id, UsuariosRequestDTO dto) {
        Usuarios usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setNome(dto.nome());
        usuario.setLogin(dto.login());
        usuario.setEmail(dto.email());
        if (dto.perfil() != null && !dto.perfil().isBlank()) {
            usuario.setPerfil(dto.perfil().toUpperCase());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setHash(passwordEncoder.encode(dto.senha()));
        }
        usuario.setDataModificacao(LocalDateTime.now());
        Usuarios atualizado = usuarioRepository.save(usuario);
        return new UsuariosResponseDTO(atualizado.getId(), atualizado.getNome(), atualizado.getLogin(), atualizado.getEmail(), atualizado.getPerfil(), atualizado.getAtivo(), atualizado.getDataCadastro());
    }

    @Transactional
    public void mudarPerfil(Long id, String novoPerfil) {
        Usuarios usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setPerfil(novoPerfil);
        usuario.setDataModificacao(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void inativar(Long id) {
        Usuarios usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public void solicitarRecuperacaoSenha(RecuperacaoSenhaRequestDTO dto) {
        Usuarios usuario = usuarioRepository.findByLogin(dto.login())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no sistema"));

        usuario.setEmail(dto.email());
        usuarioRepository.save(usuario);

        tokenRepository.deleteByUsuarioId(usuario.getId());
        TokenRecuperacao tokenObj = new TokenRecuperacao(usuario);
        tokenRepository.save(tokenObj);

        String linkRecuperacao = frontendUrl + "/redefinir-senha?token=" + tokenObj.getToken();
        emailService.enviarEmailRecuperacao(usuario.getEmail(), usuario.getNome(), linkRecuperacao);
    }

    @Transactional
    public void redefinirSenhaComToken(String token, String novaSenha) {
        TokenRecuperacao tokenObj = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Link inválido"));
        if (tokenObj.isExpirado()) {
            tokenRepository.delete(tokenObj);
            throw new RuntimeException("O link expirou.");
        }
        Usuarios usuario = tokenObj.getUsuario();
        usuario.setHash(passwordEncoder.encode(novaSenha));
        usuario.setDataModificacao(LocalDateTime.now());
        usuarioRepository.save(usuario);
        tokenRepository.delete(tokenObj);
    }
}