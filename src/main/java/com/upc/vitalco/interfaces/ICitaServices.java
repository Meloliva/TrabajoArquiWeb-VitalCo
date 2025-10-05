package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.CitaDTO;

import java.time.LocalDate;
import java.util.List;

public interface ICitaServices {
    CitaDTO registrar(CitaDTO citaDTO);
    List<CitaDTO> listarPorNutricionista(Integer idNutricionista);
    List<CitaDTO> listarPorPaciente(Integer idPaciente);
    CitaDTO actualizar(CitaDTO citaDTO);
    void eliminar(Integer id);
    String unirseACita(Integer idCita);
    List<CitaDTO> listarPorFecha(LocalDate fecha);
}
