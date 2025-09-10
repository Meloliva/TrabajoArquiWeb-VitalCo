package com.upc.vitalco.services;
import com.upc.vitalco.dto.PlanNutricionalDTO;
import com.upc.vitalco.entidades.Plannutricional;
import com.upc.vitalco.interfaces.IPlanNutricionalServices;
import com.upc.vitalco.repositorios.PlanNutricionalRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanNutricionalService implements IPlanNutricionalServices{
    @Autowired
    private PlanNutricionalRepositorio plannutricionalRepositorio;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PlanNutricionalDTO registrar(PlanNutricionalDTO dto) {
        if (dto.getId() == null) {
            Plannutricional entidad = modelMapper.map(dto, Plannutricional.class);
            entidad = plannutricionalRepositorio.save(entidad);
            return modelMapper.map(entidad, PlanNutricionalDTO.class);
        }
        return null;
    }

    @Override
    public List<PlanNutricionalDTO> findAll() {
        return plannutricionalRepositorio.findAll()
                .stream()
                .map(e -> modelMapper.map(e, PlanNutricionalDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Integer id) {
        if (plannutricionalRepositorio.existsById(id)) {
            plannutricionalRepositorio.deleteById(id);
        }
    }

    @Override
    public PlanNutricionalDTO actualizar(Integer id, PlanNutricionalDTO dto) {
        return plannutricionalRepositorio.findById(id)
                .map(existing -> {
                    existing.setDuracion(dto.getDuracion());
                    existing.setObjetivo(dto.getObjetivo());
                    Plannutricional actualizado = plannutricionalRepositorio.save(existing);
                    return modelMapper.map(actualizado, PlanNutricionalDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Plannutricional con ID " + id + " no encontrado"));
    }
}
