package com.upc.vitalco.interfaces;
import com.upc.vitalco.dto.PlanRecetaDTO;

import java.util.List;

public interface IPlanRecetaServices {
    public void eliminar(Integer id);
    public List<PlanRecetaDTO> listarPorPaciente(Integer idPaciente);

}
