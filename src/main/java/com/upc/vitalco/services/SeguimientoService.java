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
import com.upc.vitalco.repositorios.RecetaRepositorio;
import com.upc.vitalco.repositorios.SeguimientoRepositorio;
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

    @Override
    public SeguimientoDTO agregarRecetaAProgreso(Integer idPlanReceta, Long idReceta, LocalDate fechaRegistro) {
        if(fechaRegistro == null)
        {
            fechaRegistro = LocalDate.now();
        }
        Planreceta planDeReceta = planRecetaRepositorio.findById(idPlanReceta)
            .orElseThrow(() -> new RuntimeException("No existe plan de receta con el ID indicado"));
        Receta receta = recetaRepositorio.findById(idReceta)
            .orElseThrow(() -> new RuntimeException("No existe receta con el ID indicado"));

        Planalimenticio plan = planDeReceta.getIdplanalimenticio();
        if (plan == null) {
            throw new RuntimeException("El plan de receta no tiene un plan alimenticio asociado");
        }
        Optional<Seguimiento> seguimientoOpt = seguimientoRepositorio
            .buscarPorPlanRecetaYFecha(idPlanReceta, fechaRegistro);

        Seguimiento seguimiento;
        if (seguimientoOpt.isPresent()) {
            seguimiento = seguimientoOpt.get();
            double nuevasCalorias = seguimiento.getCalorias() + receta.getCalorias();
            double nuevasProteinas = seguimiento.getProteinas() + receta.getProteinas();
            double nuevasGrasas = seguimiento.getGrasas() + receta.getGrasas();
            double nuevosCarbohidratos = seguimiento.getCarbohidratos() + receta.getCarbohidratos();

            if (nuevasCalorias > plan.getCaloriasDiaria() ||
                nuevasProteinas > plan.getProteinasDiaria() ||
                nuevasGrasas > plan.getGrasasDiaria() ||
                nuevosCarbohidratos > plan.getCarbohidratosDiaria()) {
                throw new IllegalArgumentException("No puedes exceder los valores del plan alimenticio");
            }
            seguimiento.setCalorias(nuevasCalorias);
            seguimiento.setProteinas(nuevasProteinas);
            seguimiento.setGrasas(nuevasGrasas);
            seguimiento.setCarbohidratos(nuevosCarbohidratos);
        } else {
            if (receta.getCalorias() > plan.getCaloriasDiaria() ||
                receta.getProteinas() > plan.getProteinasDiaria() ||
                receta.getGrasas() > plan.getGrasasDiaria() ||
                receta.getCarbohidratos() > plan.getCarbohidratosDiaria()) {
                throw new IllegalArgumentException("No puedes exceder los valores del plan alimenticio");
            }
            seguimiento = new Seguimiento();
            seguimiento.setIdplanreceta(planDeReceta);
            seguimiento.setFecharegistro(fechaRegistro);
            seguimiento.setCalorias(receta.getCalorias());
            seguimiento.setProteinas(receta.getProteinas());
            seguimiento.setGrasas(receta.getGrasas());
            seguimiento.setCarbohidratos(receta.getCarbohidratos());
        }
        seguimiento = seguimientoRepositorio.save(seguimiento);
        return modelMapper.map(seguimiento, SeguimientoDTO.class);
    }
    @Override
    public Map<String, Double> listarCaloriasPorHorario(Integer pacienteId, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);

        Map<String, Double> caloriasPorHorario = new HashMap<>();
        for (Seguimiento s : seguimientos) {
            List<Receta> recetas = s.getIdplanreceta().getRecetas();
            if (recetas != null) {
                for (Receta receta : recetas) {
                    String horario = receta.getIdhorario().getNombre();
                    double calorias = receta.getCalorias() != null ? receta.getCalorias() : 0.0;
                    caloriasPorHorario.put(horario, caloriasPorHorario.getOrDefault(horario, 0.0) + calorias);
                }
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

    //listar por dia, aca va
    @Override//
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
    public List<SeguimientoDTO> listarPorDniYFecha(String dni, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorInicialUsernameYFecha(dni, fecha);
        return seguimientos.stream()
                .map(s -> modelMapper.map(s, SeguimientoDTO.class))
                .collect(Collectors.toList());
    }

}
