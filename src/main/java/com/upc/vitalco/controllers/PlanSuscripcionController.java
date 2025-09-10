package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.PlanSuscripcionDTO;
import com.upc.vitalco.services.PlanSuscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlanSuscripcionController {
    @Autowired
    private PlanSuscripcionService planSuscripcionService;

    @PostMapping("/registrarPlanSuscripcion")
    public PlanSuscripcionDTO registrar(@RequestBody PlanSuscripcionDTO planSuscripcionDTO){
        return planSuscripcionService.registrar(planSuscripcionDTO);
    }

    @GetMapping("/listarPlanesSuscripcion")
    public List<PlanSuscripcionDTO> findAll(){
        return planSuscripcionService.findAll();
    }

    @DeleteMapping("/eliminarPlanSuscripcion/{id}")
    public void eliminar(@PathVariable Integer id){
        planSuscripcionService.eliminarReceta(id);
    }

    @PutMapping("/editarPlanSuscripcion/{id}")
    public ResponseEntity<PlanSuscripcionDTO> editar(@PathVariable Integer id,@RequestBody PlanSuscripcionDTO planSuscripcionDTO){
        return ResponseEntity.ok(planSuscripcionService.actualizar(id, planSuscripcionDTO));
    }
}
