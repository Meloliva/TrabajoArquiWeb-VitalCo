package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.PlanAlimenticioDTO;

import java.util.List;

public interface IPlanAlimenticioServices {
    public void eliminar(Integer id);
    public List<PlanAlimenticioDTO> findAll();
    public PlanAlimenticioDTO actualizar(PlanAlimenticioDTO planAlimenticioDTO);
    public PlanAlimenticioDTO consultarPlanAlimenticio(Integer idPaciente);
}
