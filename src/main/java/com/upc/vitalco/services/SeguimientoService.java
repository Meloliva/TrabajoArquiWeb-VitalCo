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
    private PlanRecetaRecetaRepositorio planRecetaRecetaRepositorio;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private PlanAlimenticioRepositorio planAlimenticioRepositorio;

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



    @Override
    public List<RecetaDTO> listarPorDia(Integer pacienteId, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);

        return seguimientos.stream()
                .map(Seguimiento::getPlanRecetaReceta)
                .filter(Objects::nonNull)
                .map(PlanRecetaReceta::getReceta)
                .filter(Objects::nonNull)
                .map(receta -> {
                    RecetaDTO dto = new RecetaDTO();
                    dto.setIdReceta(receta.getId().longValue());
                    dto.setNombre(receta.getNombre());
                    dto.setDescripcion(receta.getDescripcion());
                    dto.setCalorias(receta.getCalorias());
                    dto.setProteinas(receta.getProteinas());
                    dto.setGrasas(receta.getGrasas());
                    dto.setCarbohidratos(receta.getCarbohidratos());
                    return dto;
                })
                .toList();
    }


    @Override
    public Map<String, Double> obtenerTotalesNutricionales(Integer pacienteId, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);

        double totalCalorias = seguimientos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getCalorias()).orElse(0.0))
                .sum();

        double totalCarbohidratos = seguimientos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getCarbohidratos()).orElse(0.0))
                .sum();

        double totalProteinas = seguimientos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getProteinas()).orElse(0.0))
                .sum();

        double totalGrasas = seguimientos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getGrasas()).orElse(0.0))
                .sum();

        Map<String, Double> totales = new HashMap<>();
        totales.put("calorias", totalCalorias);
        totales.put("carbohidratos", totalCarbohidratos);
        totales.put("proteinas", totalProteinas);
        totales.put("grasas", totalGrasas);

        return totales;
    }


    @Override
    public Map<String, Object> verificarCumplimientoDiario(String dni, LocalDate fecha) {
        Paciente paciente = pacienteRepositorio.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI " + dni + " no encontrado"));

        Map<String, Double> consumidos = obtenerTotalesNutricionales(paciente.getId(), fecha);

        Planalimenticio plan = planAlimenticioRepositorio.buscarPorPaciente(paciente.getId());
        if (plan == null) {
            throw new RuntimeException("El paciente no tiene un plan alimenticio asignado");
        }

        double reqCal = Optional.ofNullable(plan.getCaloriasDiaria()).orElse(0.0);
        double reqProt = Optional.ofNullable(plan.getProteinasDiaria()).orElse(0.0);
        double reqGrasas = Optional.ofNullable(plan.getGrasasDiaria()).orElse(0.0);
        double reqCarb = Optional.ofNullable(plan.getCarbohidratosDiaria()).orElse(0.0);

        double conCal = consumidos.getOrDefault("calorias", 0.0);
        double conProt = consumidos.getOrDefault("proteinas", 0.0);
        double conGrasas = consumidos.getOrDefault("grasas", 0.0);
        double conCarb = consumidos.getOrDefault("carbohidratos", 0.0);

        // 4. Calcular porcentajes
        double porcCal = reqCal > 0 ? (conCal / reqCal) * 100 : 0;
        double porcProt = reqProt > 0 ? (conProt / reqProt) * 100 : 0;
        double porcGrasas = reqGrasas > 0 ? (conGrasas / reqGrasas) * 100 : 0;
        double porcCarb = reqCarb > 0 ? (conCarb / reqCarb) * 100 : 0;

        // 5. Construir respuesta
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("calorias", Map.of("consumido", conCal, "requerido", reqCal, "porcentaje", porcCal));
        resultado.put("proteinas", Map.of("consumido", conProt, "requerido", reqProt, "porcentaje", porcProt));
        resultado.put("grasas", Map.of("consumido", conGrasas, "requerido", reqGrasas, "porcentaje", porcGrasas));
        resultado.put("carbohidratos", Map.of("consumido", conCarb, "requerido", reqCarb, "porcentaje", porcCarb));

        // Cumplimiento solo si todos los nutrientes >= 100%
        boolean cumplio = porcCal >= 100 && porcProt >= 100 && porcGrasas >= 100 && porcCarb >= 100;
        resultado.put("cumplio", cumplio);

        return resultado;
    }


    @Override
    public void eliminarRecetaDeSeguimiento(Integer pacienteId, Integer seguimientoId, Integer recetaId) {
        seguimientoRepositorio.eliminarRecetaDeSeguimiento(seguimientoId, recetaId, pacienteId);
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

    @Override
    public Map<String, Double> listarCaloriasPorHorario(Integer pacienteId, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);

        Map<String, Double> caloriasPorHorario = new HashMap<>();

        for (Seguimiento s : seguimientos) {
            Receta receta = s.getPlanRecetaReceta().getReceta();
            if (receta != null && receta.getIdhorario() != null) {
                String horario = receta.getIdhorario().getNombre().toLowerCase();
                double calorias = Optional.ofNullable(receta.getCalorias()).orElse(0.0);

                caloriasPorHorario.merge(horario, calorias, Double::sum);
            }
        }

        for (String h : List.of("desayuno", "snack", "almuerzo", "cena")) {
            caloriasPorHorario.putIfAbsent(h, 0.0);
        }

        return caloriasPorHorario;
    }

}
