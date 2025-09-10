package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.PlanNutricionalDTO;
import com.upc.vitalco.services.PlanNutricionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PlanNutricionalController {
    @Autowired
    private PlanNutricionalService plannutricionalService;

    @PostMapping("/registrarPlannutricional")
    public ResponseEntity<PlanNutricionalDTO> registrar(@RequestBody PlanNutricionalDTO dto) {
        PlanNutricionalDTO registrado = plannutricionalService.registrar(dto);
        return ResponseEntity.ok(registrado);
    }

    @GetMapping("/listarPlanesNutricionales")
    public ResponseEntity<List<PlanNutricionalDTO>> listar() {
        List<PlanNutricionalDTO> lista = plannutricionalService.findAll();
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/eliminarPlannutricional/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        plannutricionalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/editarPlannutricional/{id}")
    public ResponseEntity<PlanNutricionalDTO> editar(@PathVariable Integer id, @RequestBody PlanNutricionalDTO dto) {
        PlanNutricionalDTO actualizado = plannutricionalService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }
}

