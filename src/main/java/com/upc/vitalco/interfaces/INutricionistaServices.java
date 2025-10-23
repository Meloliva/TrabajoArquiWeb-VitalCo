package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.EditarNutricionistaDTO;
import com.upc.vitalco.dto.NutricionistaDTO;

import java.util.List;

public interface INutricionistaServices {
    public NutricionistaDTO registrar(NutricionistaDTO nutricionistaDTO);
    public List<NutricionistaDTO> findAll();
    public NutricionistaDTO actualizar(EditarNutricionistaDTO editarNutricionistaDTO);
}
