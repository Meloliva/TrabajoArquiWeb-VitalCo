package com.upc.vitalco.security.controllers;

import com.upc.vitalco.entidades.Usuario;
import com.upc.vitalco.repositorios.UsuarioRepositorio;
import com.upc.vitalco.security.dtos.AuthRequestDTO;
import com.upc.vitalco.security.dtos.AuthResponseDTO;
import com.upc.vitalco.security.services.CustomUserDetailsService;
import com.upc.vitalco.security.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException; // Importante
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Importante
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "${ip.frontend}", allowCredentials = "true", exposedHeaders = "Authorization")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UsuarioRepositorio usuarioRepositorio;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, UsuarioRepositorio usuarioRepositorio) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    // Cambiamos a ResponseEntity<?> para poder devolver Maps de error o AuthResponseDTO de éxito
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequestDTO authRequest) throws Exception {

        try {
            // 🛑 1. VALIDACIÓN DE CONTRASEÑA (CRÍTICO: Esto faltaba)
            // Esto compara la contraseña que llega vs la encriptada en BD
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getDni(), authRequest.getContraseña())
            );
        } catch (BadCredentialsException e) {
            // Si la contraseña no coincide, devolvemos 401 explícitamente
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "DNI o contraseña incorrectos"));
        }

        // ✅ 2. Buscar usuario por DNI (Ya sabemos que la contraseña es correcta si pasó el bloque try)
        Usuario usuario = usuarioRepositorio.findByDni(authRequest.getDni());

        // Validar que existe (por seguridad extra, aunque el authenticate ya lo valida indirectamente)
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        // ✅ 3. Validar estado del usuario
        if ("Desactivado".equalsIgnoreCase(usuario.getEstado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Tu cuenta ha sido desactivada. Seleccione 'olvidé contraseña' para recuperarla."));
        }

        // ✅ 4. Generar Token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getDni());
        final String token = jwtUtil.generateToken(userDetails);

        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", token);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO(token, roles);

        return ResponseEntity.ok().headers(responseHeaders).body(authResponseDTO);
    }
}