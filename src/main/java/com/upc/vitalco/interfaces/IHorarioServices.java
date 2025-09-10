package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.HorarioDTO;

import java.util.List;

public interface IHorarioServices {

    public HorarioDTO registrar(HorarioDTO horarioDTO);
    public void borrar(Long id);
    public List<HorarioDTO> findAll();
    public HorarioDTO actualizar(HorarioDTO horarioDTO);
}
