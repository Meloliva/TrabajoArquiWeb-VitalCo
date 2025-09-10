package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.RecetaPersonalizadaDTO;

import java.util.List;
//US02 - Listar recetas personalizadas por paciente

public interface IRecetaPersonalizadaServices {
    List<RecetaPersonalizadaDTO> obtenerRecetasPersonalizadasPorPaciente(Integer idPaciente);
}
