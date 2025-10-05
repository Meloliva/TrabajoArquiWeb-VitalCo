package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.PacienteDTO;
import com.upc.vitalco.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PacienteController {
    @Autowired
    private PacienteService pacienteService;

    @PostMapping("/registrarPaciente")
    public PacienteDTO registrar(@RequestBody PacienteDTO dto) {
        return pacienteService.registrar(dto);
    }

    @GetMapping("/listarPacientes")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PacienteDTO> findAll(){
        return pacienteService.findAll();
    }

    @DeleteMapping("/eliminarPaciente/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Integer id){
        pacienteService.eliminar(id);
    }

    @PutMapping("/editarPaciente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PacienteDTO> editar(@RequestBody PacienteDTO dto) {
        return ResponseEntity.ok(pacienteService.actualizar(dto));
    }
}
