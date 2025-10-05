package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.PlanRecetaDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.services.PlanRecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;

@RestController
@RequestMapping("/api")
public class PlanRecetaController {
    @Autowired
    private PlanRecetaService planRecetaService;

    @PreAuthorize("hasRole('NUTRICIONISTA')")
    @GetMapping("/listarPlanRecetas/{idPaciente}")
    public List<PlanRecetaDTO> listarPorPaciente(@PathVariable Integer idPaciente) {
        return planRecetaService.listarPorPaciente(idPaciente);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminarPlanReceta/{id}")
    public void eliminar(@PathVariable Integer id){
        planRecetaService.eliminar(id);
    }

    @GetMapping("/recetas/paciente/{idPaciente}/horario/{nombreHorario}")
    public List<RecetaDTO> listarRecetasPorHorarioEnPlanRecienteDePaciente(
            @PathVariable Integer idPaciente,
            @PathVariable String nombreHorario) {
        return planRecetaService.listarRecetasPorHorarioEnPlanRecienteDePaciente(idPaciente, nombreHorario);
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/autocompletarRecetas/{idPaciente}")
    public List<String> autocompletarNombreRecetaEnPlanReciente(
            @PathVariable Integer idPaciente,
            @RequestParam String texto) {
        return planRecetaService.autocompletarNombreRecetaEnPlanReciente(idPaciente, texto);
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/buscarRecetas/{idPaciente}")
    public List<RecetaDTO> buscarRecetasEnPlanReciente(
            @PathVariable Integer idPaciente,
            @RequestParam String texto) {
        return planRecetaService.buscarRecetasEnPlanReciente(idPaciente, texto);
    }

}
