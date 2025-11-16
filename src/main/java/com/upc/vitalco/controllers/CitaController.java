package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.CitaDTO;
import com.upc.vitalco.security.util.SecurityUtils;
import com.upc.vitalco.services.CitaService;
import com.upc.vitalco.services.NutricionistaService;
import com.upc.vitalco.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CitaController {
    @Autowired
    private CitaService citaService;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private PacienteService pacienteService;
    @Autowired
    private NutricionistaService nutricionistaService;

    @PostMapping("/registrarCita")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public CitaDTO registrar(@RequestBody CitaDTO citaDTO){
        return citaService.registrar(citaDTO);
    }

    @GetMapping("/listarCitasPorNutricionista/{fecha}")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public List<CitaDTO> listarPorNutricionista(@PathVariable LocalDate fecha){
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        Integer idNutricionista=nutricionistaService.obtenerIdNutricionistaPorUsuario(idUsuario);
        return citaService.listarPorNutricionista(idNutricionista, fecha);
    }

    @GetMapping("/listarCitasPorPaciente/{fecha}")
    @PreAuthorize("hasRole('PACIENTE')")
    public List<CitaDTO> listarPorPaciente(@PathVariable LocalDate fecha){
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        Integer idPaciente=pacienteService.obtenerIdPacientePorUsuario(idUsuario);
        return citaService.listarPorPaciente(idPaciente, fecha);
    }
    @PutMapping("/actualizarCita")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public CitaDTO actualizar(@RequestBody CitaDTO citaDTO) {
        return citaService.actualizar(citaDTO);
    }

    @DeleteMapping("/eliminarCita/{id}")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public void eliminar(@PathVariable Integer id) {
        citaService.eliminar(id);
    }

    @GetMapping("/unirseACita/{id}")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public ResponseEntity<String> unirseACita(@PathVariable Integer id) {
        String link = citaService.unirseACita(id);
        return ResponseEntity.ok(link);
    }
    @GetMapping("/paciente/hoy")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<CitaDTO>> listarMisCitasPacienteHoy() {
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        Integer idPaciente=pacienteService.obtenerIdPacientePorUsuario(idUsuario);
        List<CitaDTO> citas = citaService.listarPorPacienteHoy(idPaciente);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/paciente/mañana")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<CitaDTO>> listarMisCitasPacienteMañana() {
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        Integer idPaciente=pacienteService.obtenerIdPacientePorUsuario(idUsuario);
        List<CitaDTO> citas = citaService.listarPorPacienteMañana(idPaciente);
        return ResponseEntity.ok(citas);
    }
    @GetMapping("/nutricionista/hoy")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public ResponseEntity<List<CitaDTO>> listarMisCitasHoy() {
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        Integer idNutricionista=nutricionistaService.obtenerIdNutricionistaPorUsuario(idUsuario);
        List<CitaDTO> citas = citaService.listarPorNutricionistaHoy(idNutricionista);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/nutricionista/mañana")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public ResponseEntity<List<CitaDTO>> listarMisCitasMañana() {
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        Integer idNutricionista=nutricionistaService.obtenerIdNutricionistaPorUsuario(idUsuario);
        List<CitaDTO> citas = citaService.listarPorNutricionistaMañana(idNutricionista);
        return ResponseEntity.ok(citas);
    }
    @GetMapping("/verificarDisponibilidad")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @RequestParam Long nutricionistaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora
    ) {
        try {
            boolean existeCita = citaService.existeCita(nutricionistaId, dia, hora);
            return ResponseEntity.ok(existeCita);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

}
