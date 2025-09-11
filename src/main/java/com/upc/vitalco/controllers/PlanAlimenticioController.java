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
    public PlanAlimenticioDTO registrar(@RequestBody PlanAlimenticioDTO dto) {
        return planAlimenticioService.registrar(dto);
    }

    @GetMapping("/listarPlanesAlimenticios")
    public List<PlanAlimenticioDTO> findAll(){
        return planAlimenticioService.findAll();
    }

    @DeleteMapping("/eliminarPlanAlimenticio/{id}")
    public void eliminar(@PathVariable Integer id){
        planAlimenticioService.eliminar(id);
    }

    @PutMapping("/editarPlanAlimenticio")
    public ResponseEntity<PlanAlimenticioDTO> editar(@RequestBody PlanAlimenticioDTO dto) {
        return ResponseEntity.ok(planAlimenticioService.actualizar(dto));
    }
}
