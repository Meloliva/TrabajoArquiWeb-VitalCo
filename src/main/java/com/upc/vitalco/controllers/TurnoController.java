package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.TurnoDTO;
import com.upc.vitalco.services.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


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
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editarTurno")
    public ResponseEntity<TurnoDTO> actualizar(@RequestBody TurnoDTO turnoDTO) {
        return ResponseEntity.ok(turnoService.actualizar(turnoDTO));
    }
    @DeleteMapping("/eliminarTurno/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Integer id) {
        turnoService.eliminar(id);
    }
    @GetMapping("/listarTurnos")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TurnoDTO> findAll() {
        return turnoService.findAll();
    }
}
