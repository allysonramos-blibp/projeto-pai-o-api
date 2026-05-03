package com.devlapa.o_pai_o.controllers;

import com.devlapa.o_pai_o.domain.usuarios.AuthenticationDTO;
import com.devlapa.o_pai_o.domain.usuarios.LoginResponseDTO;
import com.devlapa.o_pai_o.domain.usuarios.Usuarios;
import com.devlapa.o_pai_o.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("auth")

public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var usuario = (Usuarios) auth.getPrincipal();


        var token = tokenService.generateToken(usuario);


        return ResponseEntity.ok(new LoginResponseDTO(usuario.getId(), token, usuario.getNome(), usuario.getPerfil()));
    }


}