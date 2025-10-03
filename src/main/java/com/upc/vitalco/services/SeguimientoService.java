package com.upc.vitalco.services;

import com.upc.vitalco.dto.*;
import com.upc.vitalco.entidades.*;
import com.upc.vitalco.interfaces.ISeguimientoServices;
import com.upc.vitalco.repositorios.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeguimientoService implements ISeguimientoServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SeguimientoRepositorio seguimientoRepositorio;

    @Autowired
    private PlanRecetaRepositorio planRecetaRepositorio;

    @Autowired
    private RecetaRepositorio recetaRepositorio;

    @Autowired
    private PlanRecetaRecetaRepositorio planRecetaRecetaRepositorio;

    //registrar funciona bien
    @Override
    public SeguimientoDTO agregarProgreso(Long idPlanRecetaReceta) {

        PlanRecetaReceta rel = planRecetaRecetaRepositorio.findById(idPlanRecetaReceta)
                .orElseThrow(() -> new RuntimeException("No existe PlanRecetaReceta con id: " + idPlanRecetaReceta));

        Planreceta plan = rel.getPlanreceta();
        Receta receta = rel.getReceta();

        if (plan == null) {
            throw new RuntimeException("El PlanRecetaReceta no tiene un plan asociado");
        }
        if (receta == null) {
            throw new RuntimeException("El PlanRecetaReceta no tiene una receta asociada");
        }

        if ((receta.getCalorias() != null && receta.getCalorias() > plan.getIdplanalimenticio().getCaloriasDiaria()) ||
                (receta.getProteinas() != null && receta.getProteinas() > plan.getIdplanalimenticio().getProteinasDiaria()) ||
                (receta.getGrasas() != null && receta.getGrasas() > plan.getIdplanalimenticio().getGrasasDiaria()) ||
                (receta.getCarbohidratos() != null && receta.getCarbohidratos() > plan.getIdplanalimenticio().getCarbohidratosDiaria())) {
            throw new IllegalArgumentException("La receta excede los valores nutricionales permitidos por el plan");
        }

        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setCalorias(receta.getCalorias());
        seguimiento.setCarbohidratos(receta.getCarbohidratos());
        seguimiento.setGrasas(receta.getGrasas());
        seguimiento.setProteinas(receta.getProteinas());
        seguimiento.setCumplio(false); // por defecto
        seguimiento.setFecharegistro(LocalDate.now());
        seguimiento.setPlanRecetaReceta(rel);

        Seguimiento saved = seguimientoRepositorio.save(seguimiento);

        SeguimientoDTO result = new SeguimientoDTO();
        result.setId(saved.getId());
        result.setCalorias(saved.getCalorias());
        result.setCarbohidratos(saved.getCarbohidratos());
        result.setGrasas(saved.getGrasas());
        result.setProteinas(saved.getProteinas());
        result.setCumplio(saved.getCumplio());
        result.setFecharegistro(saved.getFecharegistro());
        result.setIdPlanRecetaReceta(rel.getIdPlanRecetaReceta());

        RecetaDTO recetaDTO = new RecetaDTO();
        recetaDTO.setIdReceta(receta.getId().longValue());
        recetaDTO.setNombre(receta.getNombre());
        recetaDTO.setCalorias(receta.getCalorias());
        recetaDTO.setProteinas(receta.getProteinas());
        recetaDTO.setGrasas(receta.getGrasas());
        recetaDTO.setCarbohidratos(receta.getCarbohidratos());
        recetaDTO.setIngredientes(receta.getIngredientes());
        recetaDTO.setPreparacion(receta.getPreparacion());
        recetaDTO.setCantidadPorcion(receta.getCantidadPorcion());
        recetaDTO.setTiempo(receta.getTiempo());
        recetaDTO.setFoto(receta.getFoto());
        if (receta.getIdhorario() != null) {
            HorarioDTO horarioDTO = new HorarioDTO();
            horarioDTO.setId(receta.getIdhorario().getId().longValue());
            horarioDTO.setNombre(receta.getIdhorario().getNombre());
            recetaDTO.setIdhorario(horarioDTO);
        }
        result.setReceta(recetaDTO);

        return result;
    }



    //security
    @Override
    public Map<String, Double> listarCaloriasPorHorario(Integer pacienteId, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);

        Map<String, Double> caloriasPorHorario = new HashMap<>();
        for (Seguimiento s : seguimientos) {
            Receta receta = s.getPlanRecetaReceta().getReceta();
            if (receta != null && receta.getIdhorario() != null) {
                String horario = receta.getIdhorario().getNombre().toLowerCase();
                double calorias = receta.getCalorias() != null ? receta.getCalorias() : 0.0;
                caloriasPorHorario.put(horario, caloriasPorHorario.getOrDefault(horario, 0.0) + calorias);
            }
        }
        // Asegurar que siempre estén las claves
        for (String h : List.of("desayuno", "snack", "almuerzo", "cena")) {
            caloriasPorHorario.putIfAbsent(h, 0.0);
        }
        return caloriasPorHorario;
    }

    /*        double pctCal = plan.getCaloriasDiaria() != null && plan.getCaloriasDiaria() > 0
                ? (seguimiento.getCalorias() / plan.getCaloriasDiaria()) * 100 : 0;
        double pctPro = plan.getProteinasDiaria() != null && plan.getProteinasDiaria() > 0
                ? (seguimiento.getProteinas() / plan.getProteinasDiaria()) * 100 : 0;
        double pctGra = plan.getGrasasDiaria() != null && plan.getGrasasDiaria() > 0
                ? (seguimiento.getGrasas() / plan.getGrasasDiaria()) * 100 : 0;
        double pctCar = plan.getCarbohidratosDiaria() != null && plan.getCarbohidratosDiaria() > 0
                ? (seguimiento.getCarbohidratos() / plan.getCarbohidratosDiaria()) * 100 : 0;

        double promedioPct = (pctCal + pctPro + pctGra + pctCar) / 4.0;

        // 5) Determinar si cumplió con el rango aceptable (ejemplo: 90%-110%)
        seguimiento.setCumplio(promedioPct >= 90 && promedioPct <= 110);*/
    //security
    @Override
    public Map<String, Double> obtenerTotalesNutricionales(Integer pacienteId, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);

        double totalCalorias = 0.0;
        double totalCarbohidratos = 0.0;
        double totalProteinas = 0.0;
        double totalGrasas = 0.0;

        for (Seguimiento s : seguimientos) {
            totalCalorias += Optional.ofNullable(s.getCalorias()).orElse(0.0);
            totalCarbohidratos += Optional.ofNullable(s.getCarbohidratos()).orElse(0.0);
            totalProteinas += Optional.ofNullable(s.getProteinas()).orElse(0.0);
            totalGrasas += Optional.ofNullable(s.getGrasas()).orElse(0.0);
        }

        Map<String, Double> totales = new HashMap<>();
        totales.put("calorias", totalCalorias);
        totales.put("carbohidratos", totalCarbohidratos);
        totales.put("proteinas", totalProteinas);
        totales.put("grasas", totalGrasas);

        return totales;
    }


    //security
    @Override
    public List<CumplimientoDTO> listarCumplimientoDiario(String dni, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorDniYFecha(dni, fecha);

        double totalCalorias = 0.0;
        double totalGrasas = 0.0;
        double totalCarbohidratos = 0.0;
        double totalProteinas = 0.0;

        Planalimenticio plan = null;
        for (Seguimiento s : seguimientos) {
            totalCalorias += Optional.ofNullable(s.getCalorias()).orElse(0.0);
            totalGrasas += Optional.ofNullable(s.getGrasas()).orElse(0.0);
            totalCarbohidratos += Optional.ofNullable(s.getCarbohidratos()).orElse(0.0);
            totalProteinas += Optional.ofNullable(s.getProteinas()).orElse(0.0);

            if (plan == null && s.getPlanRecetaReceta().getPlanreceta() != null) {
                plan = s.getPlanRecetaReceta().getPlanreceta().getIdplanalimenticio();
            }
        }

        boolean cumplio = false;
        if (plan != null) {
            cumplio = totalCalorias >= plan.getCaloriasDiaria() &&
                    totalGrasas >= plan.getGrasasDiaria() &&
                    totalCarbohidratos >= plan.getCarbohidratosDiaria() &&
                    totalProteinas >= plan.getProteinasDiaria();
        }

        return List.of(new CumplimientoDTO(dni, cumplio));
    }

    @Override
    public List<SeguimientoDTO> listarPorDia(Integer pacienteId, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);

        return seguimientos.stream()
                .map(s -> {
                    SeguimientoDTO dto = new SeguimientoDTO();

                    // copiar lo básico con ModelMapper pero ignorar la parte conflictiva
                    modelMapper.map(s, dto);

                    // setear manualmente solo lo que necesitas
                    if (s.getPlanRecetaReceta() != null) {
                        dto.setIdPlanRecetaReceta(s.getPlanRecetaReceta().getIdPlanRecetaReceta());

                        if (s.getPlanRecetaReceta().getReceta() != null) {
                            dto.setReceta(modelMapper.map(s.getPlanRecetaReceta().getReceta(), RecetaDTO.class));
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());


    }

    @Override
    public SeguimientoDTO editarRequerimientos(Integer idSeguimiento, NutricionistaxRequerimientoDTO requerimientoNutriDTO) {
        Optional<Seguimiento> seguimientoOpt = seguimientoRepositorio.findById(idSeguimiento);
        if (seguimientoOpt.isPresent()) {
            Seguimiento seguimiento = seguimientoOpt.get();
            Planreceta planreceta = seguimiento.getPlanRecetaReceta().getPlanreceta();

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
    public List<SeguimientoDTO> listarPorDniYFecha(String dni, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorInicialUsernameYFecha(dni, fecha);
        return seguimientos.stream()
                .map(s -> modelMapper.map(s, SeguimientoDTO.class))
                .collect(Collectors.toList());
    }
}
