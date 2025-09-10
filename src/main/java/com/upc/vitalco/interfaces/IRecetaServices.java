package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.dto.RecetaPersonalizadaDTO;

import java.util.List;

public interface IRecetaServices {
    //CRUD recetas
    public RecetaDTO registrar(RecetaDTO recetaDTO);
    public List<RecetaDTO> findAll();
    public void eliminarReceta(Long idReceta);
    public RecetaDTO actualizar(RecetaDTO recetaDTO);

}
