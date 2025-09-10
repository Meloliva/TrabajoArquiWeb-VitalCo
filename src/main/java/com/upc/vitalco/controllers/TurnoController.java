package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.TurnoDTO;
import com.upc.vitalco.services.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TurnoController {
    @Autowired
    private TurnoService  turnoService;
    @PostMapping("/turno")
    public TurnoDTO registrar(@RequestBody TurnoDTO turnoDTO) {
        return turnoService.registrar(turnoDTO);
    }
    @PutMapping("/turno")
    public ResponseEntity<TurnoDTO> actualizar(@RequestBody TurnoDTO turnoDTO) {
        return ResponseEntity.ok(turnoService.actualizar(turnoDTO));
    }
    @DeleteMapping("/turno/{id}")
    public void eliminar(@PathVariable Long id) {
        turnoService.eliminar(id);
    }
    @GetMapping("/turno")
    public List<TurnoDTO> findAll() {
        return turnoService.findAll();
    }
}
