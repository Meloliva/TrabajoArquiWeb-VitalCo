package com.upc.vitalco.services;

import com.upc.vitalco.dto.PlanRecetaDTO;
import com.upc.vitalco.dto.RecetaDTO;
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
import java.util.ArrayList;
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


    /*@Override
    public String agregarRecetaADia(PlanRecetaDTO planRecetaDTO) {
        // Busca el Planalimenticio por su ID principal
        Planalimenticio planAlimenticio = planAlimenticioRepositorio.findById(planRecetaDTO.getIdplanalimenticio().getId())
                .orElseThrow(() -> new RuntimeException("No existe el plan alimenticio con ID: " + planRecetaDTO.getIdplanalimenticio()));

        Horario horario = horarioRepositorio.findById(planRecetaDTO.getIdhorario().getId())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        Planreceta planreceta = planRecetaRepositorio.buscarPorPacienteYFecha(
                planRecetaDTO.getIdplanalimenticio().getIdPaciente(), planRecetaDTO.getFecharegistro());

        if (planreceta == null) {
            planreceta = new Planreceta();
            planreceta.setIdplanalimenticio(planAlimenticio);
            planreceta.setIdhorario(horario);
            planreceta.setCantidadporcion(planRecetaDTO.getCantidadporcion());
            planreceta.setFecharegistro(LocalDate.now());
            // Inicializa la lista de recetas si es nula
            planreceta.setRecetas(new ArrayList<>());
        }

        for (RecetaDTO recetaDTO : planRecetaDTO.getRecetas()) {
            Receta receta = recetaRepositorio.findById(recetaDTO.getIdReceta())
                    .orElseThrow(() -> new RuntimeException("Receta no encontrada: " + recetaDTO.getIdReceta()));
            planreceta.getRecetas().add(receta);
        }

        planRecetaRepositorio.save(planreceta);
        return "Receta(s) agregada(s) correctamente al plan de receta del día";
    }*/

    // src/main/java/com/upc/vitalco/services/PlanRecetaService.java
    @Override
    public String agregarRecetaADia(PlanRecetaDTO planRecetaDTO) {
        Planalimenticio plan = planAlimenticioRepositorio.findById(planRecetaDTO.getIdplanalimenticio().getId())
                .orElseThrow(() -> new RuntimeException("No existe el plan alimenticio con ID: " + planRecetaDTO.getIdplanalimenticio().getId()));

        Horario horario = horarioRepositorio.findById(planRecetaDTO.getIdhorario().getId())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + planRecetaDTO.getIdhorario().getId()));

        Planreceta planreceta = planRecetaRepositorio
                .findByIdplanalimenticioAndIdhorario(plan, horario)
                .orElseGet(() -> {
                    Planreceta pr = new Planreceta();
                    pr.setIdplanalimenticio(plan);
                    pr.setIdhorario(horario);
                    pr.setCantidadporcion(planRecetaDTO.getCantidadporcion());
                    pr.setFecharegistro(LocalDate.now()); // solo registro
                    pr.setRecetas(new ArrayList<>());
                    return pr;
                });

        // actualiza cantidad porción si cambia
        planreceta.setCantidadporcion(planRecetaDTO.getCantidadporcion());

        for (RecetaDTO recetaDTO : planRecetaDTO.getRecetas()) {
            Receta receta = recetaRepositorio.findById(recetaDTO.getIdReceta())
                    .orElseThrow(() -> new RuntimeException("Receta no encontrada: " + recetaDTO.getIdReceta()));
            if (!planreceta.getRecetas().contains(receta)) { // evita duplicados
                planreceta.getRecetas().add(receta);
            }
        }

        planRecetaRepositorio.save(planreceta);
        return "Receta(s) agregada(s) correctamente al plan y horario seleccionados.";
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
