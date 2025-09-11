package com.upc.vitalco.interfaces;
import com.upc.vitalco.dto.PacienteDTO;

import java.util.List;
public interface IPacienteServices {
    public PacienteDTO registrar(PacienteDTO pacienteDTO);
    public void eliminar(Integer id);
    public List<PacienteDTO> findAll();
    public PacienteDTO actualizar(PacienteDTO pacienteDTO);
}
