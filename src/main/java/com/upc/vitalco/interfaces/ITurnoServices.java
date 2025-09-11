package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.TurnoDTO;

import java.util.List;

public interface ITurnoServices {
    public TurnoDTO registrar(TurnoDTO turnoDTO);
    public void eliminar(Integer id);
    public List<TurnoDTO> findAll();
    public TurnoDTO actualizar(TurnoDTO turnoDTO);
}
