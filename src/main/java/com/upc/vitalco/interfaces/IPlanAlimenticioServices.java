package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.NutricionistaRequerimientoDTO;
import com.upc.vitalco.dto.PlanAlimenticioDTO;
import com.upc.vitalco.entidades.Planalimenticio;

import java.util.List;

public interface IPlanAlimenticioServices {
    List<PlanAlimenticioDTO> findAll();
    List<PlanAlimenticioDTO> findAllConDatosActualizados();
    PlanAlimenticioDTO actualizar(PlanAlimenticioDTO planAlimenticioDTO);
    PlanAlimenticioDTO consultarPlanAlimenticio(Integer idPaciente);
    PlanAlimenticioDTO consultarPlanAlimenticioConDatosActualizados(Integer idPaciente);
    PlanAlimenticioDTO registrar(Integer idPaciente);
    NutricionistaRequerimientoDTO editarPlanAlimenticio(Integer idPlanAlimenticio, NutricionistaRequerimientoDTO nuevoPlan);
    PlanAlimenticioDTO eliminarPlanAlimenticio(Integer id);
}
