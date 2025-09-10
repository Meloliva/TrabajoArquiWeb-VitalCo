package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.PlanNutricionalDTO;

import java.util.List;

public interface IPlanNutricionalServices {
    public PlanNutricionalDTO registrar(PlanNutricionalDTO dto);
    public List<PlanNutricionalDTO> findAll();
    public void eliminar(Integer id);
    public PlanNutricionalDTO actualizar(Integer id, PlanNutricionalDTO dto);
}
