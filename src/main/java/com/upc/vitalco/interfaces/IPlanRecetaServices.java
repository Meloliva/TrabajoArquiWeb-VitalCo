package com.upc.vitalco.interfaces;
import com.upc.vitalco.dto.PlanRecetaDTO;
import com.upc.vitalco.dto.RecetaDTO;

import java.util.List;

public interface IPlanRecetaServices {
    void eliminar(Integer id);
    List<PlanRecetaDTO> listarPorPaciente(Integer idPaciente);
    List<RecetaDTO> listarRecetasPorHorarioEnPlanRecienteDePaciente(Integer idPaciente, String nombreHorario) ;
}
