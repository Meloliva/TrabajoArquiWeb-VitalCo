package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.PlanSuscripcionDTO;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

public interface IPlanSuscripcionServices {
    public PlanSuscripcionDTO registrar(PlanSuscripcionDTO PlanSuscripcionDTO);
    public List<PlanSuscripcionDTO> findAll();
    public void eliminarReceta(Integer IdPlanSuscripcion);
    public PlanSuscripcionDTO actualizar(Integer idPlanSuscripcion, PlanSuscripcionDTO PlanSuscripcionDTO);
}
