package com.upc.vitalco.services;

import com.upc.vitalco.dto.PlanRecetaDTO;
import com.upc.vitalco.entidades.Horario;
import com.upc.vitalco.entidades.Planalimenticio;
import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.entidades.Receta;
import com.upc.vitalco.interfaces.IPlanRecetaServices;
import com.upc.vitalco.repositorios.HorarioRepositorio;
import com.upc.vitalco.repositorios.PlanAlimenticioRepositorio;
import com.upc.vitalco.repositorios.PlanRecetaRepositorio;
import com.upc.vitalco.repositorios.RecetaRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanRecetaService implements IPlanRecetaServices {
    @Autowired
    private PlanRecetaRepositorio planRecetaRepositorio;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PlanAlimenticioRepositorio planAlimenticioRepositorio;
    @Autowired
    private HorarioRepositorio horarioRepositorio;
    @Autowired
    private RecetaRepositorio recetaRepositorio;


    @Override
    public String agregarRecetaADia(PlanRecetaDTO planRecetaDTO) {
        Planalimenticio planAlimenticio = planAlimenticioRepositorio.buscarPorPacienteId(planRecetaDTO.getIdplanalimenticio().getIdPaciente());
        if (planAlimenticio == null) {
            throw new RuntimeException("No existe plan alimenticio para el paciente");
        }
        Horario horario = horarioRepositorio.findById(planRecetaDTO.getIdhorario().getId())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        Receta receta = recetaRepositorio.findById(planRecetaDTO.getIdreceta().getIdReceta())
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));
        Planreceta planreceta = planRecetaRepositorio.buscarPorPacienteYFecha(planRecetaDTO.getIdplanalimenticio().getIdPaciente(), planRecetaDTO.getFecharegistro());
        if (planreceta == null) {
            planreceta = new Planreceta();
            planreceta.setIdplanalimenticio(planAlimenticio);
            planreceta.setIdhorario(horario);
            planreceta.setCantidadporcion(planreceta.getCantidadporcion());
            planreceta.setFecharegistro(LocalDate.now());
            planreceta.getRecetas().add(receta);
        }
        // Solo agregar la receta gestionada
        planreceta.getRecetas().add(receta);
        planRecetaRepositorio.save(planreceta);
        return "Receta agregada correctamente al plan de receta del día";

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
