package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.*;
import com.upc.vitalco.security.util.SecurityUtils;
import com.upc.vitalco.services.PacienteService;
import com.upc.vitalco.services.SeguimientoService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
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
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private PacienteService pacienteService;


    @PreAuthorize("hasRole('PACIENTE')")
    @PostMapping("/agregarProgreso/{idPlanRecetaReceta}")
    public ResponseEntity<SeguimientoDTO> agregarProgreso(@PathVariable Long idPlanRecetaReceta) {
        return ResponseEntity.ok(seguimientoService.agregarProgreso(idPlanRecetaReceta));
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @GetMapping("/listarSeguimientos/{fecha}")
    public ResponseEntity<SeguimientoResumenDTO> listarPorDia(
            @PathVariable("fecha") LocalDate fecha) {

        // 1. Obtener el DNI del usuario autenticado
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        Integer idPaciente = pacienteService.obtenerIdPacientePorUsuario(idUsuario);

        // 2. Obtener el DNI del paciente
        // (Necesitamos agregar este método al PacienteService o usar el repositorio)
        String dni = pacienteService.obtenerDniPorId(idPaciente);

        // 3. ✅ LLAMAR AL MÉTODO CORRECTO que usa plan histórico
        try {
            SeguimientoResumenDTO resumen = seguimientoService.resumenSeguimientoPaciente(dni, fecha);
            return ResponseEntity.ok(resumen);
        } catch (RuntimeException e) {
            // Si no hay datos, devolver estructura vacía
            SeguimientoResumenDTO vacio = new SeguimientoResumenDTO();
            vacio.setNombrePaciente("");
            vacio.setTotalesNutricionales(new HashMap<>());
            vacio.setCaloriasPorHorario(new HashMap<>());
            return ResponseEntity.ok(vacio);
        }
    }

    @PreAuthorize("hasRole('NUTRICIONISTA')")
    @GetMapping("/listarSeguimientosPorDniYFecha/{dni}/{fecha}")
    public ResponseEntity<?> listarPorDniYFecha(
            @PathVariable("dni") String dni,
            @PathVariable("fecha") LocalDate fecha
    ) {
        List<SeguimientoBusquedaDTO> resultados = seguimientoService.listarPorDniYFecha(dni, fecha);
        if (resultados.isEmpty()) {
            return ResponseEntity.ok("No hay seguimientos registrados para esa fecha.");
        }
        return ResponseEntity.ok(resultados);
    }

    @PreAuthorize("hasRole('NUTRICIONISTA')")
    @GetMapping("/cumplimiento-diario/{dni}/{fecha}")
    public ResponseEntity<Map<String, Object>> verificarCumplimientoDiario(
            @PathVariable("dni") String dni,
            @PathVariable("fecha") LocalDate fecha) {

        Map<String, Object> resultado = seguimientoService.verificarCumplimientoDiario(dni, fecha);
        return ResponseEntity.ok(resultado);
    }

    @PreAuthorize("hasRole('PACIENTE')")
    @DeleteMapping("/eliminarSeguimiento/{seguimientoId}/{recetaId}")
    public ResponseEntity<Void> eliminarRecetaDeSeguimiento(
            @PathVariable("seguimientoId") Integer seguimientoId,
            @PathVariable("recetaId") Integer recetaId) {
        Integer idUsuario = securityUtils.getUsuarioAutenticadoId();
        Integer idPaciente = pacienteService.obtenerIdPacientePorUsuario(idUsuario);

        // Llamamos al servicio modificado
        seguimientoService.eliminarRecetaDeSeguimiento(idPaciente, seguimientoId, recetaId);

        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('NUTRICIONISTA')")
    @GetMapping("/resumenSeguimientoNutriPaci/{dni}/{fecha}")
    public ResponseEntity<SeguimientoResumenDTO> resumenSeguimientoNutriPaci(
            @PathVariable("dni") String dni,
            @PathVariable("fecha") String fecha) {
        LocalDate fechaLocalDate = LocalDate.parse(fecha);
        SeguimientoResumenDTO resumen = seguimientoService.resumenSeguimientoNutri(dni, fechaLocalDate);
        return ResponseEntity.ok(resumen);
    }

    @PreAuthorize("hasRole('NUTRICIONISTA') or hasRole('PACIENTE')")
    @GetMapping("/historial")
    public ResponseEntity<List<HistorialSemanalDTO>> obtenerHistorial(
            @RequestParam String dni,
            @RequestParam(required = false) String objetivo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin
    ) {
        return ResponseEntity.ok(seguimientoService.obtenerHistorialFiltrado(dni, objetivo, fechaInicio, fechaFin));
    }
}

