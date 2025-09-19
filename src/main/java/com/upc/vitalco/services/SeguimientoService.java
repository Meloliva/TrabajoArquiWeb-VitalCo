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
        // Buscar el plan de receta por paciente y fecha
        Planreceta planDeReceta = planRecetaRepositorio.buscarPorPacienteYFecha(
                seguimientoDTO.getIdplanreceta().getIdplanalimenticio().getIdPaciente(),
                seguimientoDTO.getIdplanreceta().getFecharegistro()
        );
        if (planDeReceta == null) {
            throw new RuntimeException("No existe plan de receta para el paciente y fecha indicada");
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
    // Puedes asociar el primer planReceta como referencia, si es necesario
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
    public List<SeguimientoDTO> listarPorDia(Integer pacienteId, LocalDateTime fecha) {
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
}
