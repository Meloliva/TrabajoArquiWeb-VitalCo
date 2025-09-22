package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.PlanRecetaDTO;
import com.upc.vitalco.services.PlanRecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlanRecetaController {
    @Autowired
    private PlanRecetaService planRecetaService;

    @GetMapping("/listarPlanRecetas/{idPaciente}")
    public List<PlanRecetaDTO> listarPorPaciente(@PathVariable Integer idPaciente) {
        return planRecetaService.listarPorPaciente(idPaciente);
    }

    @DeleteMapping("/eliminarPlanReceta/{id}")
    public void eliminar(@PathVariable Integer id){
        planRecetaService.eliminar(id);
    }

    @PutMapping("/editarPlanReceta")
    public ResponseEntity<PlanRecetaDTO> editar(@RequestBody PlanRecetaDTO dto) {
        return ResponseEntity.ok(planRecetaService.actualizar(dto));
    }
}
