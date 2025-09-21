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

    @PostMapping("/registrarPlanAlimenticio")
    public ResponseEntity<String> registrar(@RequestBody PlanAlimenticioDTO planAlimenticioDTO) {
        try {

            if (planAlimenticioDTO.getIdpaciente() == null || planAlimenticioDTO.getIdpaciente().getId() == null) {
                return ResponseEntity.status(400).body("Error: idPaciente es requerido");
            }

            if (planAlimenticioDTO.getIdplanNutricional() == null || planAlimenticioDTO.getIdplanNutricional().getId() == null) {
                return ResponseEntity.status(400).body("Error: idPlanNutricional es requerido");
            }

            PlanAlimenticioDTO planCreado = planAlimenticioService.registrar(planAlimenticioDTO);

            return ResponseEntity.ok("Plan alimenticio registrado correctamente con ID: " + planCreado.getId() +
                    ". Calorías diarias calculadas: " + Math.round(planCreado.getCaloriasDiaria()) + " cal");

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
    }

    @GetMapping("/listarPlanesAlimenticios")
    public ResponseEntity<List<PlanAlimenticioDTO>> findAll() {
        try {
            List<PlanAlimenticioDTO> planes = planAlimenticioService.findAll();
            return ResponseEntity.ok(planes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/eliminarPlanAlimenticio/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            planAlimenticioService.eliminar(id);
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
            PlanAlimenticioDTO plan = planAlimenticioService.consultarPlanAlimenticio(idPaciente);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
