package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.CitaDTO;

import java.util.List;

public interface ICitaServices {
    public CitaDTO registrar(CitaDTO citaDTO);
    List<CitaDTO> listarPorNutricionista(Integer idNutricionista);
    List<CitaDTO> listarPorPaciente(Integer idPaciente);
}
