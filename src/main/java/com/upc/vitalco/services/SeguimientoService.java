package com.upc.vitalco.services;

import com.upc.vitalco.dto.NutricionistaxRequerimientoDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.dto.SeguimientoDTO;
import com.upc.vitalco.entidades.Planalimenticio;
import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.entidades.Receta;
import com.upc.vitalco.entidades.Seguimiento;
import com.upc.vitalco.interfaces.ISeguimientoServices;
import com.upc.vitalco.repositorios.PlanRecetaRepositorio;
import com.upc.vitalco.repositorios.SeguimientoRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeguimientoService implements ISeguimientoServices {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SeguimientoRepositorio seguimientoRepositorio;
    @Autowired
    private PlanRecetaRepositorio planRecetaRepositorio;
@Override
public SeguimientoDTO registrar(SeguimientoDTO seguimientoDTO) {
    Planreceta planDeReceta = planRecetaRepositorio.findById(seguimientoDTO.getIdplanreceta().getId())
            .orElseThrow(() -> new RuntimeException("No existe plan de receta con el ID indicado"));

    Optional<Seguimiento> yaExiste = seguimientoRepositorio
            .buscarPorPacienteYFecha(
                    planDeReceta.getIdplanalimenticio().getIdpaciente().getId(),
                    seguimientoDTO.getFecharegistro()
            )
            .stream()
            .filter(s -> s.getIdplanreceta().getId().equals(planDeReceta.getId()))
            .findFirst();

    if (yaExiste.isPresent()) {
        throw new RuntimeException("Ya existe un seguimiento para este plan y fecha.");
    }

    double caloriasDesayuno = 0, caloriasAlmuerzo = 0, caloriasCena = 0, caloriasSnack = 0;
    double caloriasTotales = 0, proteinasTotales = 0, grasasTotales = 0, carbohidratosTotales = 0;

    for (Receta receta : planDeReceta.getRecetas()) {
        String horario = receta.getPlanreceta().getIdhorario().getNombre() != null
                ? receta.getPlanreceta().getIdhorario().getNombre().toLowerCase(Locale.ROOT)
                : "";

        double calorias = receta.getCalorias() != null ? receta.getCalorias().doubleValue() : 0.0;
        double proteinas = receta.getProteinas() != null ? receta.getProteinas().doubleValue() : 0.0;
        double grasas = receta.getGrasas() != null ? receta.getGrasas().doubleValue() : 0.0;
        double carbohidratos = receta.getCarbohidratos() != null ? receta.getCarbohidratos().doubleValue() : 0.0;

        caloriasTotales += calorias;
        proteinasTotales += proteinas;
        grasasTotales += grasas;
        carbohidratosTotales += carbohidratos;

        switch (horario) {
            case "desayuno":
                caloriasDesayuno += calorias;
                break;
            case "almuerzo":
                caloriasAlmuerzo += calorias;
                break;
            case "cena":
                caloriasCena += calorias;
                break;
            case "snack":
                caloriasSnack += calorias;
                break;
        }
    }

    Seguimiento seguimiento = modelMapper.map(seguimientoDTO, Seguimiento.class);
    seguimiento.setIdplanreceta(planDeReceta);

    seguimiento.setCaloriasDesayuno(caloriasDesayuno);
    seguimiento.setCaloriasAlmuerzo(caloriasAlmuerzo);
    seguimiento.setCaloriasCena(caloriasCena);
    seguimiento.setCaloriasSnack(caloriasSnack);

    seguimiento.setCalorias(caloriasTotales);
    seguimiento.setProteinas(proteinasTotales);
    seguimiento.setGrasas(grasasTotales);
    seguimiento.setCarbohidratos(carbohidratosTotales);

    seguimiento = seguimientoRepositorio.save(seguimiento);
    return modelMapper.map(seguimiento, SeguimientoDTO.class);
}
    /*@Override
    public SeguimientoDTO registrar(SeguimientoDTO seguimientoDTO) {

        Planreceta planDeReceta = planRecetaRepositorio.findById(seguimientoDTO.getIdplanreceta().getId())
                .orElseThrow(() -> new RuntimeException("No existe plan de receta con el ID indicado"));

        Optional<Seguimiento> yaExiste = seguimientoRepositorio
                .buscarPorPacienteYFecha(
                        planDeReceta.getIdplanalimenticio().getIdpaciente().getId(),
                        seguimientoDTO.getFecharegistro()
                )
                .stream()
                .filter(s -> s.getIdplanreceta().getId().equals(planDeReceta.getId()))
                .findFirst();

        if (yaExiste.isPresent()) {
            throw new RuntimeException("Ya existe un seguimiento para este plan y fecha.");
        }
        double caloriasDesayuno = 0, caloriasAlmuerzo = 0, caloriasCena = 0, caloriasSnack = 0;
    double caloriasTotales = 0, proteinasTotales = 0, grasasTotales = 0, carbohidratosTotales = 0;

    for (Receta receta : planDeReceta.getRecetas()) {
        double cantidad = receta.getPlanreceta().getCantidadporcion() != null ? receta.getPlanreceta().getCantidadporcion() : 1.0;
        String horario = receta.getPlanreceta().getIdhorario().getNombre() != null ? receta.getPlanreceta().getIdhorario().getNombre().toLowerCase(Locale.ROOT) : "";

        double calorias = receta.getCalorias() != null ? receta.getCalorias().doubleValue() * cantidad : 0.0;
        double proteinas = receta.getProteinas() != null ? receta.getProteinas().doubleValue() * cantidad : 0.0;
        double grasas = receta.getGrasas() != null ? receta.getGrasas().doubleValue() * cantidad : 0.0;
        double carbohidratos = receta.getCarbohidratos() != null ? receta.getCarbohidratos().doubleValue() * cantidad : 0.0;

        caloriasTotales += calorias;
        proteinasTotales += proteinas;
        grasasTotales += grasas;
        carbohidratosTotales += carbohidratos;

        switch (horario) {
            case "desayuno":
                caloriasDesayuno += calorias;
                break;
            case "almuerzo":
                caloriasAlmuerzo += calorias;
                break;
            case "cena":
                caloriasCena += calorias;
                break;
            case "snack":
                caloriasSnack += calorias;
                break;
        }
    }

    Seguimiento seguimiento = modelMapper.map(seguimientoDTO, Seguimiento.class);
    // Asociar la entidad gestionada
    seguimiento.setIdplanreceta(planDeReceta);

    seguimiento.setCaloriasDesayuno(caloriasDesayuno);
    seguimiento.setCaloriasAlmuerzo(caloriasAlmuerzo);
    seguimiento.setCaloriasCena(caloriasCena);
    seguimiento.setCaloriasSnack(caloriasSnack);

    seguimiento.setCalorias(caloriasTotales);
    seguimiento.setProteinas(proteinasTotales);
    seguimiento.setGrasas(grasasTotales);
    seguimiento.setCarbohidratos(carbohidratosTotales);

    seguimiento = seguimientoRepositorio.save(seguimiento);
    return modelMapper.map(seguimiento, SeguimientoDTO.class);
}*/
@Override
    public void actualizarCumplimiento(Integer seguimientoId) {
        Optional<Seguimiento> seguimientoOpt = seguimientoRepositorio.findById(seguimientoId);
        if (seguimientoOpt.isPresent()) {
            Seguimiento seguimiento = seguimientoOpt.get();
            Planreceta planreceta = seguimiento.getIdplanreceta();
            if (planreceta != null) {
                Planalimenticio plan = planreceta.getIdplanalimenticio();
                if (plan != null) {
                    double caloriasSeguimiento = seguimiento.getCalorias() != null ? seguimiento.getCalorias() : 0.0;
                    double caloriasMeta = plan.getCaloriasDiaria() != null ? plan.getCaloriasDiaria() : 0.0;
                    boolean cumplio = caloriasSeguimiento >= caloriasMeta;
                    seguimiento.setCumplio(cumplio);
                    seguimientoRepositorio.save(seguimiento);
                }
            }
        }
    }
    @Override
    public List<SeguimientoDTO> listarPorDia(Integer pacienteId, LocalDate fecha) {
        // Ajusta este método según tu modelo real
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);
        return seguimientos.stream()
                .map(s -> modelMapper.map(s, SeguimientoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public SeguimientoDTO editarRequerimientos(Integer seguimientoId, NutricionistaxRequerimientoDTO requerimientoNutriDTO) {
        Optional<Seguimiento> seguimientoOpt = seguimientoRepositorio.findById(seguimientoId);
        if (seguimientoOpt.isPresent()) {
            Seguimiento seguimiento = seguimientoOpt.get();
            Planreceta planreceta = seguimiento.getIdplanreceta();

            if (planreceta != null) {
                Planalimenticio plan = planreceta.getIdplanalimenticio();
                if (plan != null && plan.getIdpaciente() != null && plan.getIdpaciente().getIdplan() != null
                        && "PREMIUM".equalsIgnoreCase(plan.getIdpaciente().getIdplan().getTipo())) {

                    if (requerimientoNutriDTO.getCalorias() == null || requerimientoNutriDTO.getCalorias() < 0 ||
                            requerimientoNutriDTO.getProteinas() == null || requerimientoNutriDTO.getProteinas() < 0 ||
                            requerimientoNutriDTO.getGrasas() == null || requerimientoNutriDTO.getGrasas() < 0 ||
                            requerimientoNutriDTO.getCarbohidratos() == null || requerimientoNutriDTO.getCarbohidratos() < 0) {
                        throw new IllegalArgumentException("Los valores de calorías, proteínas, grasas y carbohidratos deben ser no nulos y mayores o iguales a 0");
                    }

                    seguimiento.setCalorias(requerimientoNutriDTO.getCalorias());
                    seguimiento.setProteinas(requerimientoNutriDTO.getProteinas());
                    seguimiento.setGrasas(requerimientoNutriDTO.getGrasas());
                    seguimiento.setCarbohidratos(requerimientoNutriDTO.getCarbohidratos());

                    seguimiento = seguimientoRepositorio.save(seguimiento);
                    return modelMapper.map(seguimiento, SeguimientoDTO.class);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<SeguimientoDTO> listarPorInicialYFecha(String inicial, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorInicialUsernameYFecha(inicial, fecha);
        return seguimientos.stream()
                .map(s -> modelMapper.map(s, SeguimientoDTO.class))
                .collect(Collectors.toList());
    }

}
