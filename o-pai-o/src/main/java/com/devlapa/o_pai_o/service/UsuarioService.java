package com.devlapa.o_pai_o.service;

import com.devlapa.o_pai_o.domain.usuarios.Usuarios;
import com.devlapa.o_pai_o.domain.usuarios.UsuariosRequestDTO;
import com.devlapa.o_pai_o.domain.usuarios.UsuariosResponseDTO;
import com.devlapa.o_pai_o.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuariosResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(u -> new UsuariosResponseDTO(
                        u.getId(), u.getNome(), u.getLogin(),
                        u.getPerfil(), u.getAtivo(), u.getDataCadastro()
                ))
                .toList();
    }

    @Transactional
    public UsuariosResponseDTO salvar(UsuariosRequestDTO dto) {

        if (usuarioRepository.existsByLogin(dto.login())){
            throw new RuntimeException("Login já está em uso");
        }

        Usuarios usuario = new Usuarios();
        usuario.setNome(dto.nome());
        usuario.setLogin(dto.login());
        usuario.setHash(passwordEncoder.encode(dto.senha()));
        usuario.setAtivo(true);
        usuario.setDataCadastro(LocalDateTime.now());


        if (dto.perfil() != null && !dto.perfil().isBlank()) {
            usuario.setPerfil(dto.perfil().toUpperCase());
        } else {
            usuario.setPerfil("USUARIO");
        }

        Usuarios salvo = usuarioRepository.save(usuario);
        return new UsuariosResponseDTO(
                salvo.getId(), salvo.getNome(), salvo.getLogin(),
                salvo.getPerfil(), salvo.getAtivo(), salvo.getDataCadastro()
        );
    }

    @Transactional
    public UsuariosResponseDTO atualizar(Long id, UsuariosRequestDTO dto) {
        Usuarios usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setNome(dto.nome());
        usuario.setLogin(dto.login());
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setHash(passwordEncoder.encode(dto.senha()));
        }
        usuario.setDataModificacao(LocalDateTime.now());

        Usuarios atualizado = usuarioRepository.save(usuario);
        return new UsuariosResponseDTO(
                atualizado.getId(), atualizado.getNome(), atualizado.getLogin(),
                atualizado.getPerfil(), atualizado.getAtivo(), atualizado.getDataCadastro()
        );
    }

    @Transactional
    public void mudarPerfil(Long id, String novoPerfil) {
        Usuarios usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setPerfil(novoPerfil);
        usuario.setDataModificacao(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void inativar(Long id) {
        Usuarios usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(false);
        usuario.setDataModificacao(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
}