package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.RecetaDTO;

import java.util.List;

public interface IRecetaServices {
    public RecetaDTO registrar(RecetaDTO recetaDTO);
    public List<RecetaDTO> findAll();
    public void eliminarReceta(Long idReceta);
    public RecetaDTO actualizar(RecetaDTO recetaDTO);
}
