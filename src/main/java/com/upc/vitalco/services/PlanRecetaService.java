package com.upc.vitalco.services;

import com.upc.vitalco.dto.NutricionistaDTO;
import com.upc.vitalco.dto.PlanRecetaDTO;
import com.upc.vitalco.entidades.Nutricionista;
import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.interfaces.IPlanRecetaServices;
import com.upc.vitalco.repositorios.NutricionistaRepositorio;
import com.upc.vitalco.repositorios.PlanRecetaRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanRecetaService implements IPlanRecetaServices {
    @Autowired
    private PlanRecetaRepositorio planRecetaRepositorio;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PlanRecetaDTO registrar(PlanRecetaDTO planRecetaDTO) {
        if (planRecetaDTO.getId() == null) {
            Planreceta planReceta = modelMapper.map(planRecetaDTO, Planreceta.class);
            planReceta = planRecetaRepositorio.save(planReceta);
            return modelMapper.map(planReceta, PlanRecetaDTO.class);
        }
        return null;
    }

    @Override
    public void eliminar(Integer id) {
        if(planRecetaRepositorio.existsById(id)) {
            planRecetaRepositorio.deleteById(id);
        }
    }

    @Override
    public List<PlanRecetaDTO> findAll() {
        return planRecetaRepositorio.findAll()
                .stream()
                .map(planReceta -> modelMapper.map(planReceta, PlanRecetaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PlanRecetaDTO actualizar(PlanRecetaDTO planRecetaDTO) {
        return planRecetaRepositorio.findById(planRecetaDTO.getId())
                .map(existing -> {
                    Planreceta planReceta = modelMapper.map(planRecetaDTO, Planreceta.class);
                    Planreceta guardado = planRecetaRepositorio.save(planReceta);
                    return modelMapper.map(guardado, PlanRecetaDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + planRecetaDTO.getId() + " no encontrado"));
    }
}
