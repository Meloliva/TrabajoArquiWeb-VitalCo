package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.NutricionistaDTO;
import com.upc.vitalco.dto.PlanRecetaDTO;
import com.upc.vitalco.services.NutricionistaService;
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

    @PostMapping("/registrarPlanReceta")
    public PlanRecetaDTO registrar(@RequestBody PlanRecetaDTO dto) {
        return planRecetaService.registrar(dto);
    }

    @GetMapping("/listarPlanRecetas")
    public List<PlanRecetaDTO> findAll(){
        return planRecetaService.findAll();
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
