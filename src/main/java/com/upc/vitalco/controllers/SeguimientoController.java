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
    public List<RecetaDTO> listarPorDia(
            @PathVariable("pacienteId") Integer pacienteId,
            @PathVariable("fecha") LocalDate fecha) {
        return seguimientoService.listarPorDia(pacienteId, fecha);
    }

    @GetMapping("/listarSeguimientosPorDniYFecha/{dni}/{fecha}")
    public ResponseEntity<?> listarPorDniYFecha(
            @PathVariable("dni") String dni,
            @PathVariable("fecha") LocalDate fecha
    ) {
        List<SeguimientoDTO> resultados = seguimientoService.listarPorDniYFecha(dni, fecha);
        if (resultados.isEmpty()) {
            return ResponseEntity.ok("No hay seguimientos registrados para esa fecha.");
        }
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/listarCaloriasPorHorario/{idpaciente}/{fecha}")
    public ResponseEntity<?> listarCaloriasPorHorario(
            @PathVariable("idpaciente") Integer pacienteId,
            @PathVariable("fecha") LocalDate fecha) {
        Map<String, Double> resultado = seguimientoService.listarCaloriasPorHorario(pacienteId, fecha);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/listarTotalesNutricionales/{idpaciente}/{fecha}")
    public ResponseEntity<?> obtenerTotalesNutricionales(
            @PathVariable("idpaciente") Integer pacienteId,
            @PathVariable("fecha") LocalDate fecha) {
        Map<String, Double> resultado = seguimientoService.obtenerTotalesNutricionales(pacienteId, fecha);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/cumplimiento-diario/{dni}/{fecha}")
    public ResponseEntity<Map<String, Object>> verificarCumplimientoDiario(
            @PathVariable("dni") String dni,
            @PathVariable("fecha") LocalDate fecha) {

        Map<String, Object> resultado = seguimientoService.verificarCumplimientoDiario(dni, fecha);
        return ResponseEntity.ok(resultado);
    }
    @DeleteMapping("/eliminarSeguimiento/{seguimientoId}/{recetaId}/{pacienteId}")
    public ResponseEntity<Void> eliminarRecetaDeSeguimiento(
            @PathVariable("seguimientoId") Integer seguimientoId,
            @PathVariable("recetaId") Integer recetaId,
            @PathVariable("pacienteId") Integer pacienteId) {

        seguimientoService.eliminarRecetaDeSeguimiento(pacienteId, seguimientoId, recetaId);
        return ResponseEntity.noContent().build();
    }


}

