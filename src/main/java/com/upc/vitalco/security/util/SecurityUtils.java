package com.upc.vitalco.security.util;

import com.upc.vitalco.entidades.Usuario;
import com.upc.vitalco.repositorios.UsuarioRepositorio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    private final UsuarioRepositorio usuarioRepositorio;

    public SecurityUtils(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("No hay usuario autenticado");
        }

        String dni = authentication.getName();
        System.out.println("DNI extraído del token: " + dni);

        Usuario usuario = usuarioRepositorio.findByDni(dni);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado con DNI: " + dni);
        }

        return usuario;
    }

    public Integer getUsuarioAutenticadoId() {
        return getUsuarioAutenticado().getId();
    }
}
