package com.upc.vitalco.interfaces;


import com.upc.vitalco.dto.NutricionistaxRequerimientoDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.dto.SeguimientoDTO;
import com.upc.vitalco.dto.SeguimientoResumenDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ISeguimientoServices {
    public SeguimientoDTO agregarProgreso(Long idPlanRecetaReceta);
    public Map<String, Object> verificarCumplimientoDiario(String dni, LocalDate fecha);
    public List<RecetaDTO> listarPorDia(Integer pacienteId, LocalDate fecha);
    public Map<String, Double> obtenerTotalesNutricionales(Integer pacienteId, LocalDate fecha);
    SeguimientoResumenDTO resumenSeguimientoNutri(String dni, LocalDate fecha);
    public Map<String, Double> listarCaloriasPorHorario(Integer pacienteId, LocalDate fecha);
    public void eliminarRecetaDeSeguimiento(Integer seguimientoId, Integer recetaId, Integer pacienteId);
    List<SeguimientoDTO> listarPorDniYFecha(String dni, LocalDate fecha);
}
