package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.RecetaDTO;

import java.util.List;

public interface IRecetaServices {
    //CRUD recetas
     RecetaDTO registrar(RecetaDTO recetaDTO);
     List<RecetaDTO> findAll();
     void eliminarReceta(Long idReceta);
     RecetaDTO actualizar(RecetaDTO recetaDTO);
}
