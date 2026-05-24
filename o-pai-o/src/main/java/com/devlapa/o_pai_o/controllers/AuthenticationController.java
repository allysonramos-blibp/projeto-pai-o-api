package com.devlapa.o_pai_o.controllers;

import com.devlapa.o_pai_o.domain.usuarios.AuthenticationDTO;
import com.devlapa.o_pai_o.domain.usuarios.LoginResponseDTO;
import com.devlapa.o_pai_o.domain.usuarios.UsuariosRequestDTO;
import com.devlapa.o_pai_o.domain.usuarios.Usuarios;
import com.devlapa.o_pai_o.service.TokenService;
import com.devlapa.o_pai_o.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var usuario = (Usuarios) auth.getPrincipal();

        var token = tokenService.generateToken(usuario);

        return ResponseEntity.ok(new LoginResponseDTO(usuario.getId(), token, usuario.getNome(), usuario.getPerfil()));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid UsuariosRequestDTO data) {
        if (this.usuarioRepository.findByLogin(data.login()).isPresent()) {
            return ResponseEntity.badRequest().body("Este usuário já está cadastrado.");
        }

        String senhaCriptografada = new BCryptPasswordEncoder().encode(data.senha());

        Usuarios novoUsuario = new Usuarios();
        novoUsuario.setNome(data.nome());
        novoUsuario.setLogin(data.login());
        novoUsuario.setHash(senhaCriptografada);
        novoUsuario.setPerfil(data.perfil() != null ? data.perfil().toUpperCase() : "USUARIO");
        novoUsuario.setAtivo(true);

        this.usuarioRepository.save(novoUsuario);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity recuperarSenha(@RequestBody Map<String, String> payload) {
        String loginDigitado = payload.get("username");

        var usuarioOptional = this.usuarioRepository.findByLogin(loginDigitado);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Usuário não encontrado em nosso sistema.");
        }

        var usuario = usuarioOptional.get();
        String senhaPadraoCripto = new BCryptPasswordEncoder().encode("Mudar@123");
        usuario.setHash(senhaPadraoCripto);
        this.usuarioRepository.save(usuario);

        return ResponseEntity.ok().body("Sua senha foi resetada para o padrão: Mudar@123. Use-a para acessar e altere-a assim que entrar.");
    }
}