package com.upc.vitalco.services;
import com.upc.vitalco.dto.CumplimientoDTO;
import com.upc.vitalco.dto.NutricionistaxRequerimientoDTO;
import com.upc.vitalco.dto.SeguimientoDTO;
import com.upc.vitalco.entidades.Planalimenticio;
import com.upc.vitalco.entidades.PlanRecetaReceta;
import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.entidades.Receta;
import com.upc.vitalco.entidades.Seguimiento;
import com.upc.vitalco.interfaces.ISeguimientoServices;
import com.upc.vitalco.repositorios.PlanRecetaRecetaRepositorio;
import com.upc.vitalco.repositorios.PlanRecetaRepositorio;
import com.upc.vitalco.repositorios.RecetaRepositorio;
import com.upc.vitalco.repositorios.SeguimientoRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    // src/main/java/com/upc/vitalco/services/SeguimientoService.java
    @Override
    public SeguimientoDTO agregarRecetaAProgreso(Integer idPlanReceta, Long idReceta) {
        Planreceta planDeReceta = planRecetaRepositorio.findById(idPlanReceta)
                .orElseThrow(() -> new RuntimeException("No existe plan de receta con el ID indicado"));
        Receta receta = recetaRepositorio.findById(idReceta)
                .orElseThrow(() -> new RuntimeException("No existe receta con el ID indicado"));

        if (planDeReceta.getRecetas() == null ||
                planDeReceta.getRecetas().stream().noneMatch(r -> r.getId().equals(idReceta))) {
            throw new IllegalArgumentException("La receta no pertenece al plan de receta seleccionado");
        }

        Planalimenticio plan = planDeReceta.getIdplanalimenticio();
        if (plan == null) {
            throw new RuntimeException("El plan de receta no tiene un plan alimenticio asociado");
        }

        // Buscar la relación por los dos campos
        PlanRecetaReceta planRecetaReceta = planRecetaRecetaRepositorio
                .findByPlanrecetaIdAndRecetaId(idPlanReceta, idReceta)
                .orElseThrow(() -> new RuntimeException("No existe la relación PlanRecetaReceta para los IDs indicados"));

        // Aquí puedes validar si ya existe un seguimiento para esta relación y fecha, según tu lógica

        Seguimiento seguimiento = new Seguimiento();
        seguimiento.setPlanRecetaReceta(planRecetaReceta);
        seguimiento.setFecharegistro(LocalDate.now());
        seguimiento.setCalorias(receta.getCalorias());
        seguimiento.setProteinas(receta.getProteinas());
        seguimiento.setGrasas(receta.getGrasas());
        seguimiento.setCarbohidratos(receta.getCarbohidratos());

        seguimiento = seguimientoRepositorio.save(seguimiento);
        return modelMapper.map(seguimiento, SeguimientoDTO.class);
    }


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
        for (String h : List.of("desayuno", "snack", "almuerzo", "cena")) {
            caloriasPorHorario.putIfAbsent(h, 0.0);
        }
        return caloriasPorHorario;
    }

    @Override
    public Map<String, Double> obtenerTotalesNutricionales(Integer pacienteId, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);

        double totalCalorias = 0.0;
        double totalCarbohidratos = 0.0;
        double totalProteinas = 0.0;
        double totalGrasas = 0.0;

        for (Seguimiento s : seguimientos) {
            totalCalorias += s.getCalorias() != null ? s.getCalorias() : 0.0;
            totalCarbohidratos += s.getCarbohidratos() != null ? s.getCarbohidratos() : 0.0;
            totalProteinas += s.getProteinas() != null ? s.getProteinas() : 0.0;
            totalGrasas += s.getGrasas() != null ? s.getGrasas() : 0.0;
        }

        Map<String, Double> totales = new HashMap<>();
        totales.put("calorias", totalCalorias);
        totales.put("carbohidratos", totalCarbohidratos);
        totales.put("proteinas", totalProteinas);
        totales.put("grasas", totalGrasas);

        return totales;
    }

    @Override
    public List<CumplimientoDTO> listarCumplimientoDiario(String dni, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorDniYFecha(dni, fecha);

        double totalCalorias = 0.0;
        double totalGrasas = 0.0;
        double totalCarbohidratos = 0.0;
        double totalProteinas = 0.0;

        Planalimenticio plan = null;
        for (Seguimiento s : seguimientos) {
            totalCalorias += s.getCalorias() != null ? s.getCalorias() : 0.0;
            totalGrasas += s.getGrasas() != null ? s.getGrasas() : 0.0;
            totalCarbohidratos += s.getCarbohidratos() != null ? s.getCarbohidratos() : 0.0;
            totalProteinas += s.getProteinas() != null ? s.getProteinas() : 0.0;
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

        List<CumplimientoDTO> resultado = new ArrayList<>();
        resultado.add(new CumplimientoDTO(dni, cumplio));
        return resultado;
    }

    @Override
    public List<SeguimientoDTO> listarPorDia(Integer pacienteId, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);
        return seguimientos.stream()
                .map(s -> modelMapper.map(s, SeguimientoDTO.class))
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
