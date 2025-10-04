package com.upc.vitalco.interfaces;
import com.upc.vitalco.dto.PlanRecetaDTO;

import java.util.List;

public interface IPlanRecetaServices {
    void eliminar(Integer id);
    List<PlanRecetaDTO> listarPorPaciente(Integer idPaciente);

}
