package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.NutricionistaxRequerimientoDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.dto.SeguimientoDTO;
import com.upc.vitalco.services.SeguimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class SeguimientoController {

    @Autowired
    private SeguimientoService seguimientoService;

    @PostMapping("/registrarSeguimiento")
    public SeguimientoDTO registrarSeguimiento(@RequestBody SeguimientoDTO seguimientoDTO) {
        return seguimientoService.registrar(seguimientoDTO);
    }

    @GetMapping("/listarSeguimientos")
    public List<SeguimientoDTO> listarPorDia(
            @RequestParam Integer pacienteId,
            @RequestParam LocalDate fecha) {
        return seguimientoService.listarPorDia(pacienteId, fecha);
    }

    @PutMapping("/editarSeguimiento/{seguimientoId}")
    public SeguimientoDTO editarRequerimientos(
            @PathVariable Integer seguimientoId,
            @RequestBody NutricionistaxRequerimientoDTO requerimientoNutriDTO) {
        return seguimientoService.editarRequerimientos(seguimientoId, requerimientoNutriDTO);
    }
    @GetMapping("/listarSeguimientosPorInicialYFecha")
    public ResponseEntity<?> listarPorInicialYFecha(
            @RequestParam String inicial,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        List<SeguimientoDTO> resultados = seguimientoService.listarPorInicialYFecha(inicial, fecha);
        if (resultados.isEmpty()) {
            return ResponseEntity.ok("No hay seguimientos registrados para esa fecha.");
        }
        return ResponseEntity.ok(resultados);
    }

}

