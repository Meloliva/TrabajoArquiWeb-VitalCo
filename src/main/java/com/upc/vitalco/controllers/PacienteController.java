package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.EditarPacienteDTO;
import com.upc.vitalco.dto.PacienteDTO;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.entidades.Usuario;
import com.upc.vitalco.security.util.SecurityUtils;
import com.upc.vitalco.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PutMapping("/editarPaciente")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<PacienteDTO> editar(@RequestBody EditarPacienteDTO dto) {
        return ResponseEntity.ok(pacienteService.actualizar(dto));
    }

    @GetMapping("/buscarPorDni/{dni}")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public ResponseEntity<PacienteDTO> buscarPorDni(@PathVariable String dni) {
        PacienteDTO pacienteDTO = pacienteService.buscarPacientePorDni(dni);
        return ResponseEntity.ok(pacienteDTO);
    }

    @GetMapping("/buscarPorId/{id}")
    @PreAuthorize("hasRole('NUTRICIONISTA')")
    public ResponseEntity<PacienteDTO> obtenerPacientePorId(@PathVariable Integer id) {
        PacienteDTO pacienteDTO = pacienteService.buscarPorId(id);

        if (pacienteDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(pacienteDTO);
    }
}
