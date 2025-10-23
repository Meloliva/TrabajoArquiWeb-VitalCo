package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.CitaDTO;
import com.upc.vitalco.security.util.SecurityUtils;
import com.upc.vitalco.services.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CitaController {
    @Autowired
    private CitaService citaService;
    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping("/registrarCita")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public CitaDTO registrar(@RequestBody CitaDTO citaDTO){
        return citaService.registrar(citaDTO);
    }

    @GetMapping("/listarCitasPorNutricionista")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public List<CitaDTO> listarPorNutricionista(@PathVariable LocalDate fecha){
        Integer idNutricionista = securityUtils.getUsuarioAutenticadoId();
        return citaService.listarPorNutricionista(idNutricionista, fecha);
    }

    @GetMapping("/listarCitasPorPaciente")
    @PreAuthorize("hasRole('PACIENTE')")
    public List<CitaDTO> listarPorPaciente(@PathVariable LocalDate fecha){
        Integer idPaciente = securityUtils.getUsuarioAutenticadoId();
        return citaService.listarPorPaciente(idPaciente, fecha);
    }
    @PutMapping("/actualizarCita")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public CitaDTO actualizar(@RequestBody CitaDTO citaDTO) {
        return citaService.actualizar(citaDTO);
    }

    @DeleteMapping("/eliminarCita")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public void eliminar() {
        Integer id = securityUtils.getUsuarioAutenticadoId();
        citaService.eliminar(id);
    }

    @GetMapping("/unirseACita")
    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    public ResponseEntity<String> unirseACita() {
        Integer id = securityUtils.getUsuarioAutenticadoId();
        String link = citaService.unirseACita(id);
        return ResponseEntity.ok(link);
    }
    @GetMapping("/paciente/hoy")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<CitaDTO>> listarMisCitasPacienteHoy() {
        Integer idPaciente = securityUtils.getUsuarioAutenticadoId();
        List<CitaDTO> citas = citaService.listarPorPacienteHoy(idPaciente);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/paciente/mañana")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<CitaDTO>> listarMisCitasPacienteMañana() {
        Integer idPaciente = securityUtils.getUsuarioAutenticadoId();
        List<CitaDTO> citas = citaService.listarPorPacienteMañana(idPaciente);
        return ResponseEntity.ok(citas);
    }
    @GetMapping("/nutricionista/hoy")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public ResponseEntity<List<CitaDTO>> listarMisCitasHoy() {
        Integer idNutricionista = securityUtils.getUsuarioAutenticadoId();
        List<CitaDTO> citas = citaService.listarPorNutricionistaHoy(idNutricionista);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/nutricionista/mañana")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public ResponseEntity<List<CitaDTO>> listarMisCitasMañana() {
        Integer idNutricionista = securityUtils.getUsuarioAutenticadoId();
        List<CitaDTO> citas = citaService.listarPorNutricionistaMañana(idNutricionista);
        return ResponseEntity.ok(citas);
    }

}
