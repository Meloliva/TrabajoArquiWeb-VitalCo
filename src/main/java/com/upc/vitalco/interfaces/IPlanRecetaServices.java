package com.upc.vitalco.interfaces;
import com.upc.vitalco.dto.PlanRecetaDTO;
import com.upc.vitalco.dto.RecetaDTO;

import java.util.List;
import java.util.Map;

public interface IPlanRecetaServices {
    void eliminar(Integer id);
    List<PlanRecetaDTO> listarPorPaciente(Integer idPaciente);
    List<RecetaDTO> listarRecetasPorHorarioEnPlanRecienteDePaciente(Integer idPaciente, String nombreHorario) ;
    List<String> autocompletarNombreRecetaEnPlanReciente(Integer idPaciente, String texto);
    List<RecetaDTO> buscarRecetasEnPlanReciente(Integer idPaciente, String texto);
    List<Map<String, String>> listarRecetasAgregadasHoyPorPacienteId(Integer pacienteId);
    List<PlanRecetaDTO> listarFavoritosPorPaciente(Integer idPaciente);
}
