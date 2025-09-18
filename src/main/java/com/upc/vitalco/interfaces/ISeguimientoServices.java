package com.upc.vitalco.interfaces;


import com.upc.vitalco.dto.NutricionistaxRequerimientoDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.dto.SeguimientoDTO;

import java.time.LocalDate;
import java.util.List;

public interface ISeguimientoServices {
    public SeguimientoDTO registrar(SeguimientoDTO seguimientoDTO);
    public List<SeguimientoDTO> listarPorDia(Integer pacienteId, LocalDate fecha);
    public String agregarRecetaADia(Integer pacienteId, LocalDate fecha, RecetaDTO recetaDTO);
    public SeguimientoDTO editarRequerimientos(Integer seguimientoId, NutricionistaxRequerimientoDTO requerimientoNutriDTO);

}
