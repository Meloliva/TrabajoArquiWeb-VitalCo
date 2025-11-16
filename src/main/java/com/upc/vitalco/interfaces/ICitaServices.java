package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.CitaDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ICitaServices {
    CitaDTO registrar(CitaDTO citaDTO);
    List<CitaDTO> listarPorNutricionista(Integer idNutricionista, LocalDate fecha);
    List<CitaDTO> listarPorPaciente(Integer idPaciente, LocalDate fecha);
    CitaDTO actualizar(CitaDTO citaDTO);
    void eliminar(Integer id);
    String unirseACita(Integer idCita);
    List<CitaDTO> listarPorNutricionistaMañana(Integer idNutricionista);
    List<CitaDTO> listarPorNutricionistaHoy(Integer idNutricionista);
    List<CitaDTO> listarPorPacienteHoy(Integer idPaciente);
    List<CitaDTO> listarPorPacienteMañana(Integer idPaciente);
    public boolean existeCita(Long nutricionistaId, LocalDate dia, LocalTime hora);
}
