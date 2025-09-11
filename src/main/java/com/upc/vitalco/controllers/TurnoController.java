package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.TurnoDTO;
import com.upc.vitalco.services.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TurnoController {
    @Autowired
    private TurnoService  turnoService;
    @PostMapping("/registrarTurno")
    public TurnoDTO registrar(@RequestBody TurnoDTO turnoDTO) {
        return turnoService.registrar(turnoDTO);
    }
    @PutMapping("/editarTurno")
    public ResponseEntity<TurnoDTO> actualizar(@RequestBody TurnoDTO turnoDTO) {
        return ResponseEntity.ok(turnoService.actualizar(turnoDTO));
    }
    @DeleteMapping("/eliminarTurno/{id}")
    public void eliminar(@PathVariable Long id) {
        turnoService.eliminar(id);
    }
    @GetMapping("/listarTurnos")
    public List<TurnoDTO> findAll() {
        return turnoService.findAll();
    }
}
