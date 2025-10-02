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

    public String agregarRecetaADia(Integer idPlanAlimenticio) {
        Planalimenticio plan = planAlimenticioRepositorio.findById(idPlanAlimenticio)
                .orElseThrow(() -> new RuntimeException("No existe el plan alimenticio con ID: " + idPlanAlimenticio));

        // Buscar solo por plan alimenticio, no por fecha
        Planreceta planreceta = (Planreceta) planRecetaRepositorio
                .findByIdplanalimenticio(plan)
                .orElse(null);

        if (planreceta == null) {
            planreceta = new Planreceta();
            planreceta.setIdplanalimenticio(plan);
            planreceta.setFecharegistro(LocalDate.now());
            planreceta.setFavorito(false);
            planreceta.setRecetas(new ArrayList<>());
        }

        double caloriasTotal = 0, proteinasTotal = 0, grasasTotal = 0, carbohidratosTotal = 0;
        double caloriasObjetivo = plan.getCaloriasDiaria();
        double proteinasObjetivo = plan.getProteinasDiaria();
        double grasasObjetivo = plan.getGrasasDiaria();
        double carbohidratosObjetivo = plan.getCarbohidratosDiaria();

        List<Receta> todasRecetas = recetaRepositorio.findAll();
        List<Receta> recetasSeleccionadas = new ArrayList<>(planreceta.getRecetas());

        for (Receta receta : todasRecetas) {
            double cal = receta.getCalorias() != null ? receta.getCalorias() : 0.0;
            double pro = receta.getProteinas() != null ? receta.getProteinas() : 0.0;
            double gra = receta.getGrasas() != null ? receta.getGrasas() : 0.0;
            double car = receta.getCarbohidratos() != null ? receta.getCarbohidratos() : 0.0;

            if (caloriasTotal + cal <= caloriasObjetivo &&
                    proteinasTotal + pro <= proteinasObjetivo &&
                    grasasTotal + gra <= grasasObjetivo &&
                    carbohidratosTotal + car <= carbohidratosObjetivo) {

                if (!recetasSeleccionadas.contains(receta)) {
                    recetasSeleccionadas.add(receta);
                    caloriasTotal += cal;
                    proteinasTotal += pro;
                    grasasTotal += gra;
                    carbohidratosTotal += car;
                }
            }
        }
        for (Receta receta : recetasSeleccionadas) {
            List<Planreceta> planes = receta.getPlanrecetas();
            if (planes == null) {
                planes = new ArrayList<>();
            }
            if (!planes.contains(planreceta)) {
                planes.add(planreceta);
                receta.setPlanrecetas(planes);
                recetaRepositorio.save(receta);
            }
        }

        planreceta.setRecetas(recetasSeleccionadas);
        planRecetaRepositorio.save(planreceta);
        return "Recetas agregadas correctamente según condiciones del plan alimenticio.";
    }

    public void actualizarRecetasDePlan(Planreceta planreceta) {
        List<Receta> recetasActuales = planreceta.getRecetas();
        List<Receta> todasRecetas = recetaRepositorio.findAll();

        double caloriasTotal = recetasActuales.stream().mapToDouble(r -> r.getCalorias() != null ? r.getCalorias() : 0.0).sum();
        double proteinasTotal = recetasActuales.stream().mapToDouble(r -> r.getProteinas() != null ? r.getProteinas() : 0.0).sum();
        double grasasTotal = recetasActuales.stream().mapToDouble(r -> r.getGrasas() != null ? r.getGrasas() : 0.0).sum();
        double carbohidratosTotal = recetasActuales.stream().mapToDouble(r -> r.getCarbohidratos() != null ? r.getCarbohidratos() : 0.0).sum();

        double caloriasObjetivo = planreceta.getIdplanalimenticio().getCaloriasDiaria();
        double proteinasObjetivo = planreceta.getIdplanalimenticio().getProteinasDiaria();
        double grasasObjetivo = planreceta.getIdplanalimenticio().getGrasasDiaria();
        double carbohidratosObjetivo = planreceta.getIdplanalimenticio().getCarbohidratosDiaria();

        for (Receta receta : todasRecetas) {
            if (!recetasActuales.contains(receta)) {
                double cal = receta.getCalorias() != null ? receta.getCalorias() : 0.0;
                double pro = receta.getProteinas() != null ? receta.getProteinas() : 0.0;
                double gra = receta.getGrasas() != null ? receta.getGrasas() : 0.0;
                double car = receta.getCarbohidratos() != null ? receta.getCarbohidratos() : 0.0;

                if (caloriasTotal + cal <= caloriasObjetivo &&
                        proteinasTotal + pro <= proteinasObjetivo &&
                        grasasTotal + gra <= grasasObjetivo &&
                        carbohidratosTotal + car <= carbohidratosObjetivo) {

                    List<Planreceta> planes = receta.getPlanrecetas();
                    if (planes == null) {
                        planes = new ArrayList<>();
                    }
                    if (!planes.contains(planreceta)) {
                        planes.add(planreceta);
                        receta.setPlanrecetas(planes);
                        recetaRepositorio.save(receta);
                    }
                    recetasActuales.add(receta);

                    caloriasTotal += cal;
                    proteinasTotal += pro;
                    grasasTotal += gra;
                    carbohidratosTotal += car;
                }
            }
        }
        planreceta.setRecetas(recetasActuales);
        planRecetaRepositorio.save(planreceta);
    }

    @Override
    public void eliminar(Integer id) {
        if(planRecetaRepositorio.existsById(id)) {
            planRecetaRepositorio.deleteById(id);
        }
    }

    @Override
    public List<PlanRecetaDTO> listarPorPaciente(Integer idPaciente) {
        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);

        // Agrupa por idplanalimenticio y toma solo uno por cada plan alimenticio
        List<Planreceta> unicos = planes.stream()
                .collect(Collectors.toMap(
                        p -> p.getIdplanalimenticio().getId(),
                        p -> p,
                        (p1, p2) -> p1 // Si hay más de uno, toma el primero
                ))
                .values()
                .stream()
                .collect(Collectors.toList());

        for (Planreceta planreceta : unicos) {
            actualizarRecetasDePlan(planreceta);
        }

        return unicos.stream()
                .map(planReceta -> {
                    PlanRecetaDTO dto = modelMapper.map(planReceta, PlanRecetaDTO.class);
                    List<RecetaDTO> recetasDTO = planReceta.getRecetas().stream()
                            .map(receta -> modelMapper.map(receta, RecetaDTO.class))
                            .collect(Collectors.toList());
                    dto.setRecetas(recetasDTO);
                    return dto;
                }).collect(Collectors.toList());
    }
}
