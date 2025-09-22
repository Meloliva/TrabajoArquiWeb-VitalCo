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

        Planreceta planreceta = new Planreceta();
        planreceta.setIdplanalimenticio(plan);
        planreceta.setFecharegistro(LocalDate.now());
        planreceta.setRecetas(new ArrayList<>());

        Double caloriasObjetivo = plan.getCaloriasDiaria();
        Double proteinasObjetivo = plan.getProteinasDiaria();
        Double grasasObjetivo = plan.getGrasasDiaria();
        Double carbohidratosObjetivo = plan.getCarbohidratosDiaria();

        List<Receta> todasRecetas = recetaRepositorio.findAll();
        List<Receta> recetasSeleccionadas = new ArrayList<>();

        for (Receta receta : todasRecetas) {
            Double cal = receta.getCalorias() != null ? receta.getCalorias() : 0.0;
            Double pro = receta.getProteinas() != null ? receta.getProteinas() : 0.0;
            Double gra = receta.getGrasas() != null ? receta.getGrasas() : 0.0;
            Double car = receta.getCarbohidratos() != null ? receta.getCarbohidratos() : 0.0;

            if (cal < caloriasObjetivo &&
                    pro < proteinasObjetivo &&
                    gra < grasasObjetivo &&
                    car < carbohidratosObjetivo) {
                recetasSeleccionadas.add(receta);
            }
        }

        planreceta.setRecetas(recetasSeleccionadas);
        planRecetaRepositorio.save(planreceta);
        return "Recetas agregadas correctamente según condiciones del plan alimenticio.";
    }

    @Override
    public void eliminar(Integer id) {
        if(planRecetaRepositorio.existsById(id)) {
            planRecetaRepositorio.deleteById(id);
        }
    }

    @Override
    public List<PlanRecetaDTO> listarPorPaciente(Integer idPaciente) {
        return planRecetaRepositorio.buscarPorPaciente(idPaciente)
                .stream()
                .map(planReceta -> modelMapper.map(planReceta, PlanRecetaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PlanRecetaDTO actualizar(PlanRecetaDTO planRecetaDTO) {
        return planRecetaRepositorio.findById(planRecetaDTO.getId())
                .map(existing -> {
                    // Validación segura de nulls
                    boolean esPremium = false;
                    if (existing.getIdplanalimenticio() != null &&
                            existing.getIdplanalimenticio().getIdpaciente() != null &&
                            existing.getIdplanalimenticio().getIdpaciente().getIdplan() != null &&
                            existing.getIdplanalimenticio().getIdpaciente().getIdplan().getTipo() != null) {
                        esPremium = "Plan premium".equalsIgnoreCase(
                                existing.getIdplanalimenticio().getIdpaciente().getIdplan().getTipo()
                        );
                    }
                    if (!esPremium) {
                        throw new RuntimeException("Solo los usuarios premium pueden actualizar las recetas del día.");
                    }
                    existing.setRecetas(
                            planRecetaDTO.getRecetas().stream()
                                    .map(dto -> modelMapper.map(dto, Receta.class))
                                    .collect(Collectors.toList())
                    );
                    Planreceta guardado = planRecetaRepositorio.save(existing);
                    return modelMapper.map(guardado, PlanRecetaDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Plan de receta con ID " + planRecetaDTO.getId() + " no encontrado"));
    }


}
