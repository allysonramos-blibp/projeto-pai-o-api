package com.devlapa.o_pai_o.controllers;

import com.devlapa.o_pai_o.domain.usuarios.*;
import com.devlapa.o_pai_o.repositories.UsuarioRepository;
import com.devlapa.o_pai_o.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuariosController(UsuarioService usuarioService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UsuariosResponseDTO> listarTodos() {
        return usuarioRepository.findByAtivoTrue().stream()
                .map(UsuariosResponseDTO::new)
                .toList();
    }

    @PostMapping
    public ResponseEntity<UsuariosResponseDTO> cadastrar(@RequestBody UsuariosRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuariosResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody UsuariosRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @PutMapping("/{id}/alterar-senha")
    public ResponseEntity<?> alterarSenha(@PathVariable Long id, @RequestBody AlterarSenhaDTO dto) {
        var usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Usuário não encontrado.");
        }

        var usuario = usuarioOptional.get();

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getHash())) {
            return ResponseEntity.badRequest().body("A senha atual digitada está incorreta.");
        }

        String novoHash = passwordEncoder.encode(dto.novaSenha());
        usuario.setHash(novoHash);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/tornar-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> tornarAdmin(@PathVariable Long id){
        usuarioService.mudarPerfil(id, "ADMIN");
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/tornar-gerente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> tornarGerente(@PathVariable Long id){
        usuarioService.mudarPerfil(id, "GERENTE");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        usuarioService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<?> esqueciSenha(@RequestBody EsqueciSenhaDTO dto) {
        try {
            usuarioService.solicitarRecuperacaoSenha(dto.login());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@RequestBody NovaSenhaDTO dto) {
        usuarioService.redefinirSenhaComToken(dto.token(), dto.novaSenha());
        return ResponseEntity.ok().build();
    }
}