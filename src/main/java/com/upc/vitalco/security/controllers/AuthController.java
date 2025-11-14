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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.upc.vitalco.repositorios.UsuarioRepositorio;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "${ip.frontend}", allowCredentials = "true", exposedHeaders = "Authorization") //para cloud
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

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> createAuthenticationToken(@RequestBody AuthRequestDTO authRequest) throws Exception {
        // ✅ Buscar usuario por DNI
        Usuario usuario = usuarioRepositorio.findByDni(authRequest.getDni());

        // ✅ Validar que existe
        if (usuario == null) {
            Map<String, String> response = Map.of("message", "Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body((AuthResponseDTO) response);
        }

        // ✅ Validar estado del usuario
        if ("Desactivado".equalsIgnoreCase(usuario.getEstado())) {
            Map<String, String> response = Map.of("message", "Tu cuenta ha sido desactivada. Seleccione olvide contraseña para recuperarla.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body((AuthResponseDTO) response);
        }

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
