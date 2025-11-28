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
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
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

    @PostMapping("/authenticate/social")
    public ResponseEntity<?> facebookLogin(@RequestBody Map<String, String> request) {
        // 1. OBTENER EL TOKEN QUE ENVÍA ANGULAR
        // Usamos un Map para no tener que crear una clase "TokenDto"
        String token = request.get("token");

        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Token de Facebook no proporcionado"));
        }

        try {
            // 2. VERIFICAR EL TOKEN CON FACEBOOK
            // Llamamos a la API de Facebook para ver si el token es real y de quién es
            RestTemplate restTemplate = new RestTemplate();
            String facebookUrl = "https://graph.facebook.com/me?fields=email,name,picture&access_token=" + token;

            Map<String, Object> fbResponse = restTemplate.getForObject(facebookUrl, Map.class);

            if (fbResponse == null || fbResponse.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("message", "Token de Facebook inválido o expirado"));
            }

            // 3. OBTENER EL CORREO DE FACEBOOK
            String email = (String) fbResponse.get("email");
            if (email == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", "Tu cuenta de Facebook no tiene un correo visible."));
            }

            // 4. BUSCAR USUARIO EN TU BD
            // Usamos tu repositorio existente
            Usuario usuario = usuarioRepositorio.findByCorreo(email);

            if (usuario == null) {
                // OJO: En tu sistema el DNI es obligatorio y Facebook no lo da.
                // Si el usuario no existe, devolvemos un 404 especial para que el Frontend
                // redirija a una pantalla de "Completar Registro" pidiendo el DNI.
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "message", "Usuario no registrado",
                                "email", email,
                                "name", fbResponse.get("name"),
                                "photo", ((Map) ((Map) fbResponse.get("picture")).get("data")).get("url")
                        ));
            }

            // 5. SI EXISTE, GENERAR TU TOKEN JWT (Igual que en el login normal)
            if ("Desactivado".equalsIgnoreCase(usuario.getEstado())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("message", "Tu cuenta está desactivada."));
            }

            final UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getDni());
            final String jwt = jwtUtil.generateToken(userDetails);

            Set<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            // ✅ AQUÍ USAMOS TU CLASE EXISTENTE AuthResponseDTO
            return ResponseEntity.ok(new AuthResponseDTO(jwt, roles));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Error en autenticación con Facebook: " + e.getMessage()));
        }
    }
}