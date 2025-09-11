package com.upc.vitalco.services;

import com.upc.vitalco.dto.PlanAlimenticioDTO;
import com.upc.vitalco.entidades.Planalimenticio;
import com.upc.vitalco.interfaces.IPlanAlimenticioServices;
import com.upc.vitalco.repositorios.PlanAlimenticioRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PlanAlimenticioService implements IPlanAlimenticioServices {
    @Autowired
    private PlanAlimenticioRepositorio planAlimenticioRepositorio;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PlanAlimenticioDTO registrar(PlanAlimenticioDTO planAlimenticioDTO) {
        if (planAlimenticioDTO.getId() == null) {
            Planalimenticio planAlimenticio = modelMapper.map(planAlimenticioDTO, Planalimenticio.class);
            planAlimenticio = planAlimenticioRepositorio.save(planAlimenticio);
            return modelMapper.map(planAlimenticio, PlanAlimenticioDTO.class);
        }
        return null;
    }

    @Override
    public void eliminar(Integer id) {
        if(planAlimenticioRepositorio.existsById(id)) {
            planAlimenticioRepositorio.deleteById(id);
        }
    }

    @Override
    public List<PlanAlimenticioDTO> findAll() {
        return planAlimenticioRepositorio.findAll()
                .stream()
                .map(planAlimenticio -> modelMapper.map(planAlimenticio, PlanAlimenticioDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PlanAlimenticioDTO actualizar(PlanAlimenticioDTO planAlimenticioDTO) {
        return planAlimenticioRepositorio.findById(planAlimenticioDTO.getId())
                .map(existing -> {
                    Planalimenticio planAlimenticio = modelMapper.map(planAlimenticioDTO, Planalimenticio.class);
                    Planalimenticio guardado = planAlimenticioRepositorio.save(planAlimenticio);
                    return modelMapper.map(guardado, PlanAlimenticioDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + planAlimenticioDTO.getId() + " no encontrado"));
    }
}
