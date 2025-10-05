package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.PlanSuscripcionDTO;
import com.upc.vitalco.services.PlanSuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlanSuscripcionController {
    @Autowired
    private PlanSuscripcionService planSuscripcionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registrarPlanSuscripcion")
    public PlanSuscripcionDTO registrar(@RequestBody PlanSuscripcionDTO planSuscripcionDTO){
        return planSuscripcionService.registrar(planSuscripcionDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listarPlanesSuscripcion")
    public List<PlanSuscripcionDTO> findAll(){
        return planSuscripcionService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminarPlanSuscripcion/{id}")
    public void eliminar(@PathVariable Integer id){
        planSuscripcionService.eliminarReceta(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editarPlanSuscripcion")
    public ResponseEntity<PlanSuscripcionDTO> editar(@RequestBody PlanSuscripcionDTO planSuscripcionDTO){
        return ResponseEntity.ok(planSuscripcionService.actualizar(planSuscripcionDTO));
    }
}
