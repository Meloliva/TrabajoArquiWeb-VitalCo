package com.upc.vitalco.interfaces;
import com.upc.vitalco.dto.EditarPacienteDTO;
import com.upc.vitalco.dto.PacienteDTO;

import java.util.List;
public interface IPacienteServices {
    public PacienteDTO registrar(PacienteDTO pacienteDTO);
    public List<PacienteDTO> findAll();
    public PacienteDTO actualizar(EditarPacienteDTO editarPacienteDTO);
    public PacienteDTO obtenerPorUsuario(Integer idUsuario);
}
