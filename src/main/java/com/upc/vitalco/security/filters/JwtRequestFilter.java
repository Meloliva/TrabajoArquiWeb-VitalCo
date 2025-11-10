package com.upc.vitalco.security.filters;

import com.upc.vitalco.security.services.CustomUserDetailsService;
import com.upc.vitalco.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestPath = request.getRequestURI();
        if (
                requestPath.startsWith("/api/registrarUsuario") ||
                requestPath.startsWith("/api/registrarNutricionista") ||
                requestPath.startsWith("/api/registrarPaciente") ||
                requestPath.startsWith("/api/registrarRol") ||
                requestPath.startsWith("/api/recuperarCuenta") ||
                requestPath.startsWith("/api/verificarCodigoRecuperacion") ||
                requestPath.startsWith("/api/restablecerCuenta") ||
                requestPath.startsWith("/api/listarRoles") ||
                requestPath.startsWith("/api/listarPlanesSuscripcion") ||
                requestPath.startsWith("/api/listarPlanesNutricionales") ||
                requestPath.startsWith("/api/listarTurnos"))  {
            chain.doFilter(request, response);
            return;
        }


        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
            System.out.println("USERNAME:" + username);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
