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
    private PacienteRepositorio pacienteRepositorio;
    @Autowired
    private SeguimientoRepositorio seguimientoRepositorio;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PlanAlimenticioRepositorio planAlimenticioRepositorio;
    @Autowired
    private RecetaRepositorio recetaRepositorio;
    @Autowired
    private PlanRecetaRecetaRepositorio planrecetaRecetaRepositorio;

    // ✅ SOLUCIÓN 1: Obtener SOLO el plan ACTIVO más reciente
    // Un plan se considera "activo" si su Plan Alimenticio fue creado recientemente
    private Planreceta obtenerPlanActivo(Integer idPaciente) {
        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);

        if (planes.isEmpty()) {
            throw new RuntimeException("No existe plan receta para el paciente");
        }

        // Obtener el plan asociado al Plan Alimenticio MÁS RECIENTE
        return planes.stream()
                .max(Comparator.comparing(p -> {
                    LocalDate fechaCreacion = p.getIdplanalimenticio().getFechaCreacion();
                    // Si no tiene fecha, usar fecha de registro del plan receta
                    return fechaCreacion != null ? fechaCreacion : p.getFecharegistro();
                }))
                .orElseThrow(() -> new RuntimeException("No se pudo determinar el plan activo"));
    }

    public Planreceta crearPlanReceta(Integer idPlanAlimenticio) {
        Planalimenticio plan = planAlimenticioRepositorio.findById(idPlanAlimenticio)
                .orElseThrow(() -> new RuntimeException("No existe el plan alimenticio con ID: " + idPlanAlimenticio));

        Planreceta planreceta = new Planreceta();
        planreceta.setIdplanalimenticio(plan);
        planreceta.setFecharegistro(LocalDate.now());
        planreceta.setFavorito(false);
        planreceta = planRecetaRepositorio.save(planreceta);

        return planreceta;
    }

    public String asignarRecetasAPlan(Integer idPlanReceta) {
        Planreceta planreceta = planRecetaRepositorio.findById(idPlanReceta)
                .orElseThrow(() -> new RuntimeException("No existe el plan receta con ID: " + idPlanReceta));

        Planalimenticio plan = planreceta.getIdplanalimenticio();
        double caloriasObjetivo = plan.getCaloriasDiaria();

        List<Receta> todasRecetas = recetaRepositorio.findAll();
        List<PlanRecetaReceta> relacionesActuales = planrecetaRecetaRepositorio.findByPlanreceta(planreceta);
        Set<Integer> recetasActualesIds = relacionesActuales.stream()
                .map(rel -> rel.getReceta().getId())
                .collect(Collectors.toSet());

        for (Receta receta : todasRecetas) {
            double cal = Optional.ofNullable(receta.getCalorias()).orElse(0.0);
            if (cal <= caloriasObjetivo && !recetasActualesIds.contains(receta.getId())) {
                PlanRecetaReceta nuevaRelacion = new PlanRecetaReceta();
                nuevaRelacion.setPlanreceta(planreceta);
                nuevaRelacion.setReceta(receta);
                planrecetaRecetaRepositorio.save(nuevaRelacion);
            }
        }
        return "Recetas asignadas correctamente.";
    }

    public void recalcularPlanRecetas(Integer idPlan) {
        Planreceta nuevoPlanreceta = crearPlanReceta(idPlan);
        asignarRecetasAPlan(nuevoPlanreceta.getId());
    }

    @Override
    public void eliminar(Integer id) {
        if (planRecetaRepositorio.existsById(id)) {
            planRecetaRepositorio.deleteById(id);
        }
    }

    // ✅ SOLUCIÓN 2: Listar SOLO del plan ACTIVO
    @Override
    public List<PlanRecetaDTO> listarPorPaciente(Integer idPaciente) {
        // 1. Obtener SOLO el plan activo
        Planreceta planActivo = obtenerPlanActivo(idPaciente);

        // 2. Calcular consumos de HOY
        LocalDate hoy = LocalDate.now();
        List<Seguimiento> consumosHoy = seguimientoRepositorio.buscarPorPacienteYFecha(idPaciente, hoy);

        double conCal = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCalorias()).orElse(0.0)).sum();
        double conPro = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getProteinas()).orElse(0.0)).sum();
        double conGra = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getGrasas()).orElse(0.0)).sum();
        double conCar = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCarbohidratos()).orElse(0.0)).sum();

        PlanRecetaDTO dto = modelMapper.map(planActivo, PlanRecetaDTO.class);
        Planalimenticio planAlim = planActivo.getIdplanalimenticio();

        // 3. Calcular SALDOS
        double salCal = Math.max(0, Optional.ofNullable(planAlim.getCaloriasDiaria()).orElse(2000.0) - conCal);
        double salPro = Math.max(0, Optional.ofNullable(planAlim.getProteinasDiaria()).orElse(0.0) - conPro);
        double salGra = Math.max(0, Optional.ofNullable(planAlim.getGrasasDiaria()).orElse(0.0) - conGra);
        double salCar = Math.max(0, Optional.ofNullable(planAlim.getCarbohidratosDiaria()).orElse(0.0) - conCar);

        final double fSalCal = salCal;
        final double fSalPro = salPro;
        final double fSalGra = salGra;
        final double fSalCar = salCar;

        List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planActivo);

        // Lógica Plan Free
        String tipoPlan = planActivo.getIdplanalimenticio().getIdpaciente().getIdplan().getTipo();
        if ("Plan free".equalsIgnoreCase(tipoPlan) && relaciones.size() > 15) {
            relaciones = relaciones.subList(0, 15);
        }

        // 4. FILTRADO ESTRICTO
        List<PlanRecetaRecetaDTO> relacionesDTO = relaciones.stream()
                .filter(rel -> {
                    Receta r = rel.getReceta();
                    double rCal = Optional.ofNullable(r.getCalorias()).orElse(0.0);
                    double rPro = Optional.ofNullable(r.getProteinas()).orElse(0.0);
                    double rGra = Optional.ofNullable(r.getGrasas()).orElse(0.0);
                    double rCar = Optional.ofNullable(r.getCarbohidratos()).orElse(0.0);

                    return rCal <= fSalCal && rPro <= fSalPro && rGra <= fSalGra && rCar <= fSalCar;
                })
                .map(rel -> {
                    PlanRecetaRecetaDTO relDTO = new PlanRecetaRecetaDTO();
                    relDTO.setIdPlanRecetaReceta(rel.getIdPlanRecetaReceta());
                    relDTO.setIdPlanReceta(rel.getPlanreceta().getId());
                    relDTO.setFavorito(rel.getFavorito());
                    relDTO.setRecetaDTO(modelMapper.map(rel.getReceta(), RecetaDTO.class));
                    return relDTO;
                }).collect(Collectors.toList());

        dto.setRecetas(relacionesDTO);

        // Retornar lista con un solo elemento (el plan activo)
        return Collections.singletonList(dto);
    }

    // ✅ SOLUCIÓN 3: Búsqueda en el plan ACTIVO
    @Override
    public List<RecetaDTO> buscarRecetasEnPlanReciente(Integer idPaciente, String texto) {
        Planreceta planActivo = obtenerPlanActivo(idPaciente);

        String textoLower = texto.toLowerCase();
        List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planActivo);

        return relaciones.stream()
                .map(PlanRecetaReceta::getReceta)
                .filter(receta ->
                        (receta.getNombre() != null && receta.getNombre().toLowerCase().contains(textoLower)) ||
                                (receta.getPreparacion() != null && receta.getPreparacion().toLowerCase().contains(textoLower)) ||
                                (receta.getDescripcion() != null && receta.getDescripcion().toLowerCase().contains(textoLower)) ||
                                (receta.getIdhorario() != null && receta.getIdhorario().getNombre() != null &&
                                        receta.getIdhorario().getNombre().toLowerCase().contains(textoLower)) ||
                                (receta.getIngredientes() != null && receta.getIngredientes().toLowerCase().contains(textoLower))
                )
                .map(receta -> modelMapper.map(receta, RecetaDTO.class))
                .collect(Collectors.toList());
    }

    // ✅ SOLUCIÓN 4: Autocompletar en el plan ACTIVO
    @Override
    public List<String> autocompletarNombreRecetaEnPlanReciente(Integer idPaciente, String texto) {
        Planreceta planActivo = obtenerPlanActivo(idPaciente);

        List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planActivo);
        return relaciones.stream()
                .map(PlanRecetaReceta::getReceta)
                .map(Receta::getNombre)
                .filter(nombre -> nombre != null && nombre.toLowerCase().contains(texto.toLowerCase()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<RecetaDTO> listarRecetasPorHorarioEnPlanRecienteDePaciente(Integer idPaciente, String nombreHorario) {
        Planreceta planActivo = obtenerPlanActivo(idPaciente);

        // Calcular consumos
        LocalDate hoy = LocalDate.now();
        List<Seguimiento> consumosHoy = seguimientoRepositorio.buscarPorPacienteYFecha(idPaciente, hoy);

        double conCal = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCalorias()).orElse(0.0)).sum();
        double conPro = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getProteinas()).orElse(0.0)).sum();
        double conGra = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getGrasas()).orElse(0.0)).sum();
        double conCar = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCarbohidratos()).orElse(0.0)).sum();

        Planalimenticio planAlim = planActivo.getIdplanalimenticio();
        double salCal = Math.max(0, Optional.ofNullable(planAlim.getCaloriasDiaria()).orElse(0.0) - conCal);
        double salPro = Math.max(0, Optional.ofNullable(planAlim.getProteinasDiaria()).orElse(0.0) - conPro);
        double salGra = Math.max(0, Optional.ofNullable(planAlim.getGrasasDiaria()).orElse(0.0) - conGra);
        double salCar = Math.max(0, Optional.ofNullable(planAlim.getCarbohidratosDiaria()).orElse(0.0) - conCar);

        final double fSalCal = salCal;
        final double fSalPro = salPro;
        final double fSalGra = salGra;
        final double fSalCar = salCar;

        List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planActivo);
        return relaciones.stream()
                .map(PlanRecetaReceta::getReceta)
                .filter(receta -> receta.getIdhorario().getNombre().equalsIgnoreCase(nombreHorario))
                .filter(r -> {
                    double rCal = Optional.ofNullable(r.getCalorias()).orElse(0.0);
                    double rPro = Optional.ofNullable(r.getProteinas()).orElse(0.0);
                    double rGra = Optional.ofNullable(r.getGrasas()).orElse(0.0);
                    double rCar = Optional.ofNullable(r.getCarbohidratos()).orElse(0.0);

                    return rCal <= fSalCal && rPro <= fSalPro && rGra <= fSalGra && rCar <= fSalCar;
                })
                .map(receta -> modelMapper.map(receta, RecetaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> listarRecetasAgregadasHoyPorPacienteId(Integer pacienteId) {
        if (pacienteId == null) {
            throw new IllegalArgumentException("id de paciente inválido");
        }

        Paciente paciente = pacienteRepositorio.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente con ID " + pacienteId + " no encontrado"));

        LocalDate hoy = LocalDate.now();
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(paciente.getId(), hoy);

        if (seguimientos == null || seguimientos.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Seguimiento s : seguimientos) {
            if (s == null || s.getPlanRecetaReceta() == null) continue;
            Receta receta = s.getPlanRecetaReceta().getReceta();

            if (receta != null) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("seguimientoId", s.getId());
                m.put("recetaId", receta.getId());
                m.put("nombre", receta.getNombre());
                m.put("descripcion", Optional.ofNullable(receta.getDescripcion()).orElse(""));

                resultado.add(m);
            }
        }

        return resultado;
    }

    @Override
    public List<PlanRecetaDTO> listarFavoritosPorPaciente(Integer idPaciente) {
        Planreceta planActivo = obtenerPlanActivo(idPaciente);

        LocalDate hoy = LocalDate.now();
        List<Seguimiento> consumosHoy = seguimientoRepositorio.buscarPorPacienteYFecha(idPaciente, hoy);

        double conCal = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCalorias()).orElse(0.0)).sum();
        double conPro = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getProteinas()).orElse(0.0)).sum();
        double conGra = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getGrasas()).orElse(0.0)).sum();
        double conCar = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCarbohidratos()).orElse(0.0)).sum();

        Planalimenticio planAlim = planActivo.getIdplanalimenticio();

        double salCal = Math.max(0, Optional.ofNullable(planAlim.getCaloriasDiaria()).orElse(2000.0) - conCal);
        double salPro = Math.max(0, Optional.ofNullable(planAlim.getProteinasDiaria()).orElse(0.0) - conPro);
        double salGra = Math.max(0, Optional.ofNullable(planAlim.getGrasasDiaria()).orElse(0.0) - conGra);
        double salCar = Math.max(0, Optional.ofNullable(planAlim.getCarbohidratosDiaria()).orElse(0.0) - conCar);

        final double fSalCal = salCal;
        final double fSalPro = salPro;
        final double fSalGra = salGra;
        final double fSalCar = salCar;

        List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planActivo);

        List<PlanRecetaRecetaDTO> favs = relaciones.stream()
                .filter(rel -> Boolean.TRUE.equals(rel.getFavorito()))
                .filter(rel -> {
                    Receta r = rel.getReceta();
                    double rCal = Optional.ofNullable(r.getCalorias()).orElse(0.0);
                    double rPro = Optional.ofNullable(r.getProteinas()).orElse(0.0);
                    double rGra = Optional.ofNullable(r.getGrasas()).orElse(0.0);
                    double rCar = Optional.ofNullable(r.getCarbohidratos()).orElse(0.0);

                    return rCal <= fSalCal && rPro <= fSalPro && rGra <= fSalGra && rCar <= fSalCar;
                })
                .map(rel -> {
                    PlanRecetaRecetaDTO relDTO = new PlanRecetaRecetaDTO();
                    relDTO.setIdPlanRecetaReceta(rel.getIdPlanRecetaReceta());
                    relDTO.setIdPlanReceta(rel.getPlanreceta().getId());
                    relDTO.setFavorito(true);
                    relDTO.setRecetaDTO(modelMapper.map(rel.getReceta(), RecetaDTO.class));
                    return relDTO;
                }).collect(Collectors.toList());

        if (favs.isEmpty()) return Collections.emptyList();

        PlanRecetaDTO dto = modelMapper.map(planActivo, PlanRecetaDTO.class);
        dto.setRecetas(favs);
        return Collections.singletonList(dto);
    }

    @Override
    public PlanRecetaRecetaDTO actualizarFavorito(Long idPlanRecetaReceta, Boolean favorito) {
        PlanRecetaReceta relacion = planrecetaRecetaRepositorio.findById(idPlanRecetaReceta)
                .orElseThrow(() -> new RuntimeException("No existe la relación con ID: " + idPlanRecetaReceta));

        relacion.setFavorito(Boolean.TRUE.equals(favorito));
        PlanRecetaReceta actualizada = planrecetaRecetaRepositorio.save(relacion);

        PlanRecetaRecetaDTO dto = new PlanRecetaRecetaDTO();
        dto.setIdPlanRecetaReceta(actualizada.getIdPlanRecetaReceta());
        dto.setIdPlanReceta(actualizada.getPlanreceta().getId());
        dto.setFavorito(actualizada.getFavorito());

        RecetaDTO recetaDTO = modelMapper.map(actualizada.getReceta(), RecetaDTO.class);
        dto.setRecetaDTO(recetaDTO);

        return dto;
    }
}