package com.upc.vitalco.services;

import com.upc.vitalco.dto.PlanSuscripcionDTO;
import com.upc.vitalco.entidades.Plansuscripcion;
import com.upc.vitalco.interfaces.IPlanSuscripcionServices;
import com.upc.vitalco.repositorios.PlanSuscripcionRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanSuscripcionService implements IPlanSuscripcionServices {
    @Autowired
    private PlanSuscripcionRepositorio planSuscripcionRepositorio;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PlanSuscripcionDTO registrar(PlanSuscripcionDTO planSuscripcionDTO) {
        if (planSuscripcionDTO.getId() == null) {
            Plansuscripcion planSuscripcion = modelMapper.map(planSuscripcionDTO, Plansuscripcion.class);
            Plansuscripcion guardado = planSuscripcionRepositorio.save(planSuscripcion);
            return modelMapper.map(guardado, PlanSuscripcionDTO.class);
        }
        return null;
    }

    @Override
    public List<PlanSuscripcionDTO> findAll() {
        return planSuscripcionRepositorio.findAll()
                .stream()
                .map(plan -> modelMapper.map(plan, PlanSuscripcionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarReceta(Integer idPlanSuscripcion) {
        if (planSuscripcionRepositorio.existsById(idPlanSuscripcion)) {
            planSuscripcionRepositorio.deleteById(idPlanSuscripcion);
        }
    }

    @Override
    public PlanSuscripcionDTO actualizar(Integer idPlanSuscripcion, PlanSuscripcionDTO planSuscripcionDTO) {
        return planSuscripcionRepositorio.findById(idPlanSuscripcion)
                .map(existing -> {
                    existing.setTipo(planSuscripcionDTO.getTipo());
                    existing.setPrecio(planSuscripcionDTO.getPrecio());
                    Plansuscripcion guardado = planSuscripcionRepositorio.save(existing);
                    return modelMapper.map(guardado, PlanSuscripcionDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("PlanSuscripcion con ID " + planSuscripcionDTO.getId() +
                        " no encontrado"));
    }
}
