package com.example.PAYYact4_t4.controller;

import com.example.PAYYact4_t4.dto.RespuestaAutenticacion;
import com.example.PAYYact4_t4.dto.SolicitudLogin;
import com.example.PAYYact4_t4.dto.SolicitudRegistro;
import com.example.PAYYact4_t4.model.Usuario;
import com.example.PAYYact4_t4.repository.UsuarioRepository;
import com.example.PAYYact4_t4.security.UtilidadJwt;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder codificadorPassword;

    @Autowired
    private UtilidadJwt utilidadJwt;

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody SolicitudRegistro solicitud) {
        if (usuarioRepository.existsByEmail(solicitud.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "El email ya está registrado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        Usuario nuevoUsuario = new Usuario(
                solicitud.getNombre(),
                solicitud.getEmail(),
                codificadorPassword.encode(solicitud.getPassword())
        );

        usuarioRepository.save(nuevoUsuario);

        String token = utilidadJwt.generarToken(nuevoUsuario.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RespuestaAutenticacion(token, nuevoUsuario.getEmail(), nuevoUsuario.getNombre()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@Valid @RequestBody SolicitudLogin solicitud) {
        Usuario usuario = usuarioRepository.findByEmail(solicitud.getEmail()).orElse(null);

        if (usuario == null || !codificadorPassword.matches(solicitud.getPassword(), usuario.getPassword())) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Credenciales inválidas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        String token = utilidadJwt.generarToken(usuario.getEmail());
        return ResponseEntity.ok(new RespuestaAutenticacion(token, usuario.getEmail(), usuario.getNombre()));
    }
}