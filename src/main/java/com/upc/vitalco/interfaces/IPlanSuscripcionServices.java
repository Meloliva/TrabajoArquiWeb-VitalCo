package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.PlanSuscripcionDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPlanSuscripcionServices {
    public PlanSuscripcionDTO registrar(PlanSuscripcionDTO PlanSuscripcionDTO);
    public List<PlanSuscripcionDTO> findAll();
    public void eliminarReceta(Integer IdPlanSuscripcion);
    public PlanSuscripcionDTO actualizar(PlanSuscripcionDTO PlanSuscripcionDTO);
}
