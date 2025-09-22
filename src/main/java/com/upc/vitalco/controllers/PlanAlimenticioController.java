package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.NutricionistaDTO;
import com.upc.vitalco.dto.PlanAlimenticioDTO;
import com.upc.vitalco.services.NutricionistaService;
import com.upc.vitalco.services.PlanAlimenticioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlanAlimenticioController {
    @Autowired
    private PlanAlimenticioService planAlimenticioService;

    @GetMapping("/listarPlanesAlimenticios")
    public ResponseEntity<List<PlanAlimenticioDTO>> findAll() {
        try {

            List<PlanAlimenticioDTO> planes = planAlimenticioService.findAllConDatosActualizados();
            return ResponseEntity.ok(planes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/eliminarPlanAlimenticio/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            planAlimenticioService.eliminarPlanAlimenticio(id);
            return ResponseEntity.ok("Plan alimenticio eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Plan alimenticio no encontrado con ID: " + id);
        }
    }

    @PutMapping("/editarPlanAlimenticio")
    public ResponseEntity<PlanAlimenticioDTO> editar(@RequestBody PlanAlimenticioDTO dto) {
        try {
            if (dto.getId() == null) {
                return ResponseEntity.status(400).body(null);
            }

            PlanAlimenticioDTO planActualizado = planAlimenticioService.actualizar(dto);
            return ResponseEntity.ok(planActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    @GetMapping("/consultarPlanAlimenticio/{idPaciente}")
    public ResponseEntity<PlanAlimenticioDTO> consultarPlanAlimenticio(@PathVariable Integer idPaciente) {
        try {

            PlanAlimenticioDTO plan = planAlimenticioService.consultarPlanAlimenticioConDatosActualizados(idPaciente);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
