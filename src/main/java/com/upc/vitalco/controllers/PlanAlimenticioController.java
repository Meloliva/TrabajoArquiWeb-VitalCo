package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.NutricionistaDTO;
import com.upc.vitalco.dto.NutricionistaRequerimientoDTO;
import com.upc.vitalco.dto.PlanAlimenticioDTO;
import com.upc.vitalco.entidades.Planalimenticio;
import com.upc.vitalco.security.util.SecurityUtils;
import com.upc.vitalco.services.NutricionistaService;
import com.upc.vitalco.services.PlanAlimenticioService;
import com.upc.vitalco.services.PlanRecetaService;
import com.upc.vitalco.services.SeguimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlanAlimenticioController {
    @Autowired
    private PlanAlimenticioService planAlimenticioService;
    @Autowired
    private SeguimientoService seguimientoService;
    @Autowired
    private PlanRecetaService planRecetaService;
    @Autowired
    private SecurityUtils securityUtils;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listarPlanesAlimenticios")
    public ResponseEntity<List<PlanAlimenticioDTO>> findAll() {
        try {

            List<PlanAlimenticioDTO> planes = planAlimenticioService.findAllConDatosActualizados();
            return ResponseEntity.ok(planes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminarPlanAlimenticio/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            planAlimenticioService.eliminarPlanAlimenticio(id);
            return ResponseEntity.ok("Plan alimenticio eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Plan alimenticio no encontrado con ID: " + id);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
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


    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/consultarPlanAlimenticio")
    public ResponseEntity<PlanAlimenticioDTO> consultarPlanAlimenticio() {
        Integer idPaciente = securityUtils.getUsuarioAutenticadoId();
        try {

            PlanAlimenticioDTO plan = planAlimenticioService.consultarPlanAlimenticioConDatosActualizados(idPaciente);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PreAuthorize("hasRole('NUTRICIONISTA')")
    @PutMapping("/editarNutrientes/{dniPaciente}")
    public ResponseEntity<NutricionistaRequerimientoDTO> editarPlanAlimenticio(
            @PathVariable("dniPaciente") String dni,@RequestBody NutricionistaRequerimientoDTO nutricionistaRequerimientoDTO) {
        NutricionistaRequerimientoDTO actualizado = planAlimenticioService.editarPlanAlimenticio(dni,nutricionistaRequerimientoDTO);

        Planalimenticio plan = planAlimenticioService.obtenerPlanAlimenticioPorPaciente(dni);
        Integer idPlan = plan.getId();
        planRecetaService.recalcularPlanRecetas(idPlan);

        return ResponseEntity.ok(actualizado);
    }
}
