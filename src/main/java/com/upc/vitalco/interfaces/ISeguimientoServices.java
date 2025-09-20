package com.upc.vitalco.interfaces;


import com.upc.vitalco.dto.NutricionistaxRequerimientoDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.dto.SeguimientoDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ISeguimientoServices {
    public SeguimientoDTO registrar(SeguimientoDTO seguimientoDTO);
    public void actualizarCumplimiento(Integer seguimientoId);
    public List<SeguimientoDTO> listarPorDia(Integer pacienteId, LocalDate fecha);
    public SeguimientoDTO editarRequerimientos(Integer seguimientoId, NutricionistaxRequerimientoDTO requerimientoNutriDTO);

}
