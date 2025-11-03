package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.PlanRecetaDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.security.util.SecurityUtils;
import com.upc.vitalco.services.PlanRecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlanRecetaController {
    @Autowired
    private PlanRecetaService planRecetaService;
    @Autowired
    private SecurityUtils securityUtils;

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/listarPlanRecetas")
    public List<PlanRecetaDTO> listarPorPaciente() {
        Integer idPaciente = securityUtils.getUsuarioAutenticadoId();
        return planRecetaService.listarPorPaciente(idPaciente);
    }
    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/listarPlanRecetasFavoritos")
    public List<PlanRecetaDTO> listarFavoritosPorPaciente() {
        Integer idPaciente = securityUtils.getUsuarioAutenticadoId();
        return planRecetaService.listarFavoritosPorPaciente(idPaciente);
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @PutMapping("/actualizarPlanReceta/{id}/favorito")
    public ResponseEntity<PlanRecetaDTO> actualizarFavorito(
            @PathVariable Integer id,
            @RequestParam Boolean favorito) {
        try {
            PlanRecetaDTO actualizado = planRecetaService.actualizarFavorito(id, favorito);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminarPlanReceta/{id}")
    public void eliminar(@PathVariable Integer id){
        planRecetaService.eliminar(id);
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/listarRecetasPorHorarios/{nombreHorario}")
    public List<RecetaDTO> listarRecetasPorHorarioEnPlanRecienteDePaciente(
            @PathVariable String nombreHorario) {
        Integer idPaciente = securityUtils.getUsuarioAutenticadoId();
        return planRecetaService.listarRecetasPorHorarioEnPlanRecienteDePaciente(idPaciente, nombreHorario);
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/autocompletarRecetas/{texto}")
    public List<String> autocompletarNombreRecetaEnPlanReciente(
            @PathVariable("texto") String texto) {
        Integer idPaciente = securityUtils.getUsuarioAutenticadoId();
        return planRecetaService.autocompletarNombreRecetaEnPlanReciente(idPaciente, texto);
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/buscarRecetas/{texto}")
    public List<RecetaDTO> buscarRecetasEnPlanReciente(
            @PathVariable("texto") String texto) {
        Integer idPaciente = securityUtils.getUsuarioAutenticadoId();
        return planRecetaService.buscarRecetasEnPlanReciente(idPaciente, texto);
    }
    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/listarRecetasAgregadasHoy")
    public List<Map<String, String>> listarRecetasAgregadasHoy() {
        Integer idPaciente = securityUtils.getUsuarioAutenticadoId();
        return planRecetaService.listarRecetasAgregadasHoyPorPacienteId(idPaciente);
    }


}
