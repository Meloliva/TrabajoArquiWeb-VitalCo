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
    @Override
    public String agregarRecetaADia(PlanRecetaDTO planRecetaDTO) {
        Planalimenticio plan = planAlimenticioRepositorio.findById(planRecetaDTO.getIdplanalimenticio().getId())
                .orElseThrow(() -> new RuntimeException("No existe el plan alimenticio con ID: " + planRecetaDTO.getIdplanalimenticio().getId()));

        Planreceta planreceta = new Planreceta();
        planreceta.setIdplanalimenticio(plan);
        planreceta.setCantidadporcion(planRecetaDTO.getCantidadporcion());
        planreceta.setFecharegistro(LocalDate.now());
        planreceta.setRecetas(new ArrayList<>());

        // Objetivos nutricionales del plan alimenticio
        Double caloriasObjetivo = plan.getCaloriasDiaria();
        Double proteinasObjetivo = plan.getProteinasDiaria();
        Double grasasObjetivo = plan.getGrasasDiaria();
        Double carbohidratosObjetivo = plan.getCarbohidratosDiaria();

        // Selección automática de recetas
        List<Receta> todasRecetas = recetaRepositorio.findAll();
        List<Receta> recetasSeleccionadas = new ArrayList<>();
        Double sumaCal = 0.0, sumaPro = 0.0, sumaGra = 0.0, sumaCar = 0.0;

        for (Receta receta : todasRecetas) {
            Double cal = receta.getCalorias().doubleValue();
            Double pro = receta.getProteinas().doubleValue();
            Double gra = receta.getGrasas().doubleValue();
            Double car = receta.getCarbohidratos().doubleValue();

            if (sumaCal + cal <= caloriasObjetivo &&
                sumaPro + pro <= proteinasObjetivo &&
                sumaGra + gra <= grasasObjetivo &&
                sumaCar + car <= carbohidratosObjetivo) {
                recetasSeleccionadas.add(receta);
                sumaCal += cal;
                sumaPro += pro;
                sumaGra += gra;
                sumaCar += car;
            }
            if (sumaCal >= caloriasObjetivo &&
                sumaPro >= proteinasObjetivo &&
                sumaGra >= grasasObjetivo &&
                sumaCar >= carbohidratosObjetivo) {
                break;
            }
        }

        planreceta.setRecetas(recetasSeleccionadas);

        planRecetaRepositorio.save(planreceta);
        return "Recetas automáticas agregadas correctamente según condiciones del paciente.";
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
