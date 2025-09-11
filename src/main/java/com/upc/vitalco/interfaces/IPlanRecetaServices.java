package com.upc.vitalco.interfaces;
import com.upc.vitalco.dto.PlanRecetaDTO;

import java.util.List;

public interface IPlanRecetaServices {
    public PlanRecetaDTO registrar(PlanRecetaDTO planRecetaDTO);
    public void eliminar(Integer id);
    public List<PlanRecetaDTO> findAll();
    public PlanRecetaDTO actualizar(PlanRecetaDTO planRecetaDTO);
}
