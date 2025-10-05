package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.CitaDTO;
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

    @PostMapping("/registrarCita")
    @PreAuthorize("hasRole('ADMIN')")
    public CitaDTO registrar(@RequestBody CitaDTO citaDTO){
        return citaService.registrar(citaDTO);
    }

    @GetMapping("/listarCitasPorNutricionista/{idNutricionista}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CitaDTO> listarPorNutricionista(@PathVariable Integer idNutricionista){
        return citaService.listarPorNutricionista(idNutricionista);
    }

    @GetMapping("/listarCitasPorPaciente/{idPaciente}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CitaDTO> listarPorPaciente(@PathVariable Integer idPaciente){
        return citaService.listarPorPaciente(idPaciente);
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
    public ResponseEntity<String> unirseACita(@PathVariable Integer id) {
        String link = citaService.unirseACita(id);
        return ResponseEntity.ok(link);
    }

    @GetMapping("/ListarCitasPorFecha/{fecha}")
    public List<CitaDTO> listarPorFecha(
            @PathVariable("fecha") LocalDate fecha) {
        return citaService.listarPorFecha(fecha);
    }

}
