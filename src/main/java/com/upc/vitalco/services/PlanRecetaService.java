package com.upc.vitalco.services;

import com.upc.vitalco.dto.PlanRecetaDTO;
import com.upc.vitalco.dto.PlanRecetaRecetaDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.entidades.*;
import com.upc.vitalco.interfaces.IPlanRecetaServices;
import com.upc.vitalco.repositorios.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
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
    @Autowired
    private PlanRecetaRecetaRepositorio planrecetaRecetaRepositorio;

    public Planreceta crearPlanReceta(Integer idPlanAlimenticio) {
        Planalimenticio plan = planAlimenticioRepositorio.findById(idPlanAlimenticio)
                .orElseThrow(() -> new RuntimeException("No existe el plan alimenticio con ID: " + idPlanAlimenticio));

        // Verificar si ya existe un planreceta para ese plan alimenticio
        Planreceta planreceta = (Planreceta) planRecetaRepositorio.findByIdplanalimenticio(plan).orElse(null);

        if (planreceta == null) {
            planreceta = new Planreceta();
            planreceta.setIdplanalimenticio(plan);
            planreceta.setFecharegistro(LocalDate.now());
            planreceta.setFavorito(false);
            planreceta = planRecetaRepositorio.save(planreceta);
        }

        return planreceta;
    }
    public String asignarRecetasAPlan(Integer idPlanReceta) {
        Planreceta planreceta = planRecetaRepositorio.findById(idPlanReceta)
                .orElseThrow(() -> new RuntimeException("No existe el plan receta con ID: " + idPlanReceta));

        Planalimenticio plan = planreceta.getIdplanalimenticio();

        double caloriasObjetivo = plan.getCaloriasDiaria();
        double proteinasObjetivo = plan.getProteinasDiaria();
        double grasasObjetivo = plan.getGrasasDiaria();
        double carbohidratosObjetivo = plan.getCarbohidratosDiaria();

        List<Receta> todasRecetas = recetaRepositorio.findAll();
        List<PlanRecetaReceta> relacionesActuales = planrecetaRecetaRepositorio.findByPlanreceta(planreceta);
        Set<Integer> recetasActualesIds = relacionesActuales.stream()
                .map(rel -> rel.getReceta().getId())
                .collect(Collectors.toSet());

        for (Receta receta : todasRecetas) {
            double cal = receta.getCalorias() != null ? receta.getCalorias() : 0.0;
            double pro = receta.getProteinas() != null ? receta.getProteinas() : 0.0;
            double gra = receta.getGrasas() != null ? receta.getGrasas() : 0.0;
            double car = receta.getCarbohidratos() != null ? receta.getCarbohidratos() : 0.0;

            boolean cumple = cal <= caloriasObjetivo &&
                    pro <= proteinasObjetivo &&
                    gra <= grasasObjetivo &&
                    car <= carbohidratosObjetivo;

            if (cumple && !recetasActualesIds.contains(receta.getId())) {
                PlanRecetaReceta nuevaRelacion = new PlanRecetaReceta();
                nuevaRelacion.setPlanreceta(planreceta);
                nuevaRelacion.setReceta(receta);
                planrecetaRecetaRepositorio.save(nuevaRelacion);
            }
        }

        return "Recetas asignadas correctamente al plan.";
    }


    public void actualizarRecetasDePlan(Planreceta planreceta) {
        if (planreceta.getId() == null) {
            planreceta = planRecetaRepositorio.save(planreceta);
        }

        List<PlanRecetaReceta> relacionesActuales = planrecetaRecetaRepositorio.findByPlanreceta(planreceta);
        Set<Integer> recetasActualesIds = relacionesActuales.stream()
                .map(rel -> rel.getReceta().getId())
                .collect(Collectors.toSet());

        List<Receta> todasRecetas = recetaRepositorio.findAll();

        double caloriasObjetivo = planreceta.getIdplanalimenticio().getCaloriasDiaria();
        double proteinasObjetivo = planreceta.getIdplanalimenticio().getProteinasDiaria();
        double grasasObjetivo = planreceta.getIdplanalimenticio().getGrasasDiaria();
        double carbohidratosObjetivo = planreceta.getIdplanalimenticio().getCarbohidratosDiaria();

        for (Receta receta : todasRecetas) {
            if (!recetasActualesIds.contains(receta.getId())) {
                double cal = receta.getCalorias() != null ? receta.getCalorias() : 0.0;
                double pro = receta.getProteinas() != null ? receta.getProteinas() : 0.0;
                double gra = receta.getGrasas() != null ? receta.getGrasas() : 0.0;
                double car = receta.getCarbohidratos() != null ? receta.getCarbohidratos() : 0.0;

                if (cal <= caloriasObjetivo &&
                        pro <= proteinasObjetivo &&
                        gra <= grasasObjetivo &&
                        car <= carbohidratosObjetivo) {

                    PlanRecetaReceta nuevaRelacion = new PlanRecetaReceta();
                    nuevaRelacion.setPlanreceta(planreceta);
                    nuevaRelacion.setReceta(receta);
                    planrecetaRecetaRepositorio.save(nuevaRelacion);
                }
            }
        }
    }

    @Override
    public void eliminar(Integer id) {
        if (planRecetaRepositorio.existsById(id)) {
            planRecetaRepositorio.deleteById(id);
        }
    }

    @Override
    public List<PlanRecetaDTO> listarPorPaciente(Integer idPaciente) {
        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);

        // Quitar duplicados por plan alimenticio
        List<Planreceta> unicos = planes.stream()
                .collect(Collectors.toMap(
                        p -> p.getIdplanalimenticio().getId(),
                        p -> p,
                        (p1, p2) -> p1
                ))
                .values()
                .stream()
                .collect(Collectors.toList());

        for (int i = 0; i < unicos.size(); i++) {
            Planreceta planreceta = unicos.get(i);

            // Si no tiene ID, lo guardamos primero
            if (planreceta.getId() == null) {
                planreceta = planRecetaRepositorio.save(planreceta);
                unicos.set(i, planreceta);
            }

            // 🔹 Aquí llamamos al método asignador
            asignarRecetasAPlan(planreceta.getId());
        }

        // Convertir a DTO
        return unicos.stream()
                .map(planReceta -> {
                    PlanRecetaDTO dto = modelMapper.map(planReceta, PlanRecetaDTO.class);

                    List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planReceta);
                    List<RecetaDTO> recetasDTO = relaciones.stream()
                            .map(rel -> modelMapper.map(rel.getReceta(), RecetaDTO.class))
                            .collect(Collectors.toList());

                    dto.setRecetas(recetasDTO);
                    return dto;
                }).collect(Collectors.toList());
    }



}
