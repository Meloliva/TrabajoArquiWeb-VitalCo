package com.upc.vitalco.interfaces;


import com.upc.vitalco.dto.CumplimientoDTO;
import com.upc.vitalco.dto.NutricionistaxRequerimientoDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.dto.SeguimientoDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ISeguimientoServices {
    public SeguimientoDTO agregarRecetaAProgreso(Integer idPlanReceta, Long idReceta, LocalDate fechaRegistro);
    public List<CumplimientoDTO> listarCumplimientoDiario(String dni, LocalDate fecha);
    public List<SeguimientoDTO> listarPorDia(Integer pacienteId, LocalDate fecha);
    public Map<String, Double> obtenerTotalesNutricionales(Integer pacienteId, LocalDate fecha);
    public SeguimientoDTO editarRequerimientos(Integer seguimientoId, NutricionistaxRequerimientoDTO requerimientoNutriDTO);
    List<SeguimientoDTO> listarPorDniYFecha(String dni, LocalDate fecha);
    public Map<String, Double> listarCaloriasPorHorario(Integer pacienteId, LocalDate fecha);
}
