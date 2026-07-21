package com.example.PAYYact4_t4.security;

import com.example.PAYYact4_t4.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class FiltroJwt extends OncePerRequestFilter {

    @Autowired
    private UtilidadJwt utilidadJwt;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String encabezadoAutorizacion = request.getHeader("Authorization");

        if (encabezadoAutorizacion != null && encabezadoAutorizacion.startsWith("Bearer ")) {
            String token = encabezadoAutorizacion.substring(7);

            if (utilidadJwt.validarToken(token)) {
                String email = utilidadJwt.obtenerEmailDelToken(token);

                if (usuarioRepository.existsByEmail(email)) {
                    UsernamePasswordAuthenticationToken autenticacion =
                            new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}