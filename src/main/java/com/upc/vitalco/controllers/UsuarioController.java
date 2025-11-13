package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.*;
import com.upc.vitalco.security.util.SecurityUtils;
import com.upc.vitalco.services.NutricionistaService;
import com.upc.vitalco.services.PacienteService;
import com.upc.vitalco.services.UsuarioService;
import com.upc.vitalco.security.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private PacienteService pacienteService;
    @Autowired
    private NutricionistaService nutricionistaService;

    @PostMapping("/registrarUsuario")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody UsuarioDTO dto) {
        UsuarioDTO registrado = usuarioService.registrar(dto);
        return ResponseEntity.ok(registrado);
    }

    @GetMapping("/usuarioNormal")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UsuarioDTO> listarPorUsuario() {
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        Integer idPaciente=pacienteService.obtenerIdPacientePorUsuario(idUsuario);
        UsuarioDTO lista = usuarioService.obtenerPorId(idPaciente);
        return ResponseEntity.ok(lista);
    }
    @GetMapping("/usuarioPaciente")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<PacienteDTO> obtenerDatosPaciente() {
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();

        // ✅ Obtener el objeto Paciente asociado al usuario autenticado
        PacienteDTO pacienteDTO = pacienteService.obtenerPorUsuario(idUsuario);

        return ResponseEntity.ok(pacienteDTO);
    }
    @GetMapping("/usuarioNutricionista")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public ResponseEntity<UsuarioDTO> listarPorNutricionista() {
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        Integer idNutricionista=nutricionistaService.obtenerIdNutricionistaPorUsuario(idUsuario);
        UsuarioDTO lista = usuarioService.obtenerPorIdNutri(idNutricionista);
        return ResponseEntity.ok(lista);
    }

    // 🟢 Nuevo endpoint para obtener el nutricionista por usuario
    @GetMapping("/nutricionistaPorUsuario/{idUsuario}")
    public ResponseEntity<NutricionistaDTO> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        NutricionistaDTO dto = nutricionistaService.obtenerPorUsuario(idUsuario);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/eliminarUsuario")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public ResponseEntity<Void> eliminar() {
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        usuarioService.eliminar(idUsuario);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/recuperarCuenta")
    public ResponseEntity<?> solicitarRecuperacion(@RequestParam String correo) {
            usuarioService.solicitarRecuperacion(correo);
            return ResponseEntity.ok("Se envió el código de recuperación al correo.");
    }
    @PostMapping("/verificarCodigoRecuperacion")
    public ResponseEntity<Map<String, Object>> verificarCodigo(@RequestBody VerificarCodigoDTO verificarCodigoDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean valido = usuarioService.verificarCodigo(
                    verificarCodigoDTO.getCodigo()
            );

            if (valido) {
                response.put("valido", true);
                response.put("mensaje", "Código válido.");
                return ResponseEntity.ok(response);
            } else {
                response.put("valido", false);
                response.put("mensaje", "Código inválido o expirado.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("valido", false);
            response.put("mensaje", "Error al verificar el código: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @PostMapping("/restablecerCuenta")
    public ResponseEntity<?> restablecerCuenta(@RequestBody RestablecerCuentaDTO restablecerCuentaDTO) {
        usuarioService.restablecerCuenta(restablecerCuentaDTO.getCorreo(), restablecerCuentaDTO.getNuevaContrasena());
            return ResponseEntity.ok("Cuenta restablecida y activada.");
    }

    @GetMapping("/obtenerUsuario")
    @PreAuthorize("hasRole('PACIENTE') or hasRole('NUTRICIONISTA')")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId() {
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        UsuarioDTO usuario = usuarioService.obtenerPorIdIndividual(idUsuario);
        return ResponseEntity.ok(usuario);
    }

}
