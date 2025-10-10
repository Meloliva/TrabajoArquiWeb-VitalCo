package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.UsuarioDTO;
import com.upc.vitalco.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrarUsuario")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody UsuarioDTO dto) {
        UsuarioDTO registrado = usuarioService.registrar(dto);
        return ResponseEntity.ok(registrado);
    }

    @GetMapping("/listarUsuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> lista = usuarioService.findAll();
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/eliminarUsuario/{id}")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/recuperarCuenta")
    public ResponseEntity<?> solicitarRecuperacion(@RequestParam String correo) {
            usuarioService.solicitarRecuperacion(correo);
            return ResponseEntity.ok("Se envió el código de recuperación al correo.");
    }
    @PostMapping("/verificarCodigoRecuperacion")
    public ResponseEntity<?> verificarCodigo(@RequestParam String correo, @RequestParam String codigo) {
            boolean valido = usuarioService.verificarCodigo(correo, codigo);
            if (valido) {
                return ResponseEntity.ok("Código válido.");
            } else {
                return ResponseEntity.badRequest().body("Código inválido.");
            }
    }
    @PostMapping("/restablecerCuenta")
    public ResponseEntity<?> restablecerCuenta(
                @RequestParam String correo,
                @RequestParam String nuevaContraseña,
                @RequestParam String codigo) {
            usuarioService.restablecerCuenta(correo, nuevaContraseña, codigo);
            return ResponseEntity.ok("Cuenta restablecida y activada.");
    }


}
