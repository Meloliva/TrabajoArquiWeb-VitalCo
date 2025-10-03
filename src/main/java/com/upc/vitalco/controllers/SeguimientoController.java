package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.*;
import com.upc.vitalco.services.SeguimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class SeguimientoController {

    @Autowired
    private SeguimientoService seguimientoService;

    @PostMapping("/agregarProgreso/{idPlanRecetaReceta}")
    public ResponseEntity<SeguimientoDTO> agregarProgreso(@PathVariable Long idPlanRecetaReceta) {
        return ResponseEntity.ok(seguimientoService.agregarProgreso(idPlanRecetaReceta));
    }

    @GetMapping("/listarSeguimientos/{pacienteId}/{fecha}")
    public List<SeguimientoDTO> listarPorDia(
            @PathVariable("pacienteId") Integer pacienteId,
            @PathVariable("fecha") LocalDate fecha) {
        return seguimientoService.listarPorDia(pacienteId, fecha);
    }

    @PutMapping("/editarSeguimiento/{seguimientoId}")
    public SeguimientoDTO editarRequerimientos(
            @PathVariable Integer seguimientoId,
            @RequestBody NutricionistaxRequerimientoDTO requerimientoNutriDTO) {
        return seguimientoService.editarRequerimientos(seguimientoId, requerimientoNutriDTO);
    }
    @GetMapping("/listarSeguimientosPorDniYFecha/{dni}/{fecha}")
    public ResponseEntity<?> listarPorDniYFecha(
            @PathVariable("dni") String dni,
            @PathVariable("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        List<SeguimientoDTO> resultados = seguimientoService.listarPorDniYFecha(dni, fecha);
        if (resultados.isEmpty()) {
            return ResponseEntity.ok("No hay seguimientos registrados para esa fecha.");
        }
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/listarCaloriasPorHorario")
    public ResponseEntity<?> listarCaloriasPorHorario(
            @RequestParam Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Map<String, Double> resultado = seguimientoService.listarCaloriasPorHorario(pacienteId, fecha);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/listarTotalesNutricionales")
    public ResponseEntity<?> obtenerTotalesNutricionales(
            @RequestParam Integer pacienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Map<String, Double> resultado = seguimientoService.obtenerTotalesNutricionales(pacienteId, fecha);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/listarCumplimientoDiario")
    public ResponseEntity<List<CumplimientoDTO>> listarCumplimientoDiario(
            @RequestParam String dniPaciente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<CumplimientoDTO> resultado = seguimientoService.listarCumplimientoDiario(
                dniPaciente, fecha);
        return ResponseEntity.ok(resultado);
    }

}

