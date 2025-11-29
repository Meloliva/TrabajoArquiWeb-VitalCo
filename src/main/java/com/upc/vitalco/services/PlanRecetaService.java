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
    private HorarioRepositorio horarioRepositorio;
    @Autowired
    private RecetaRepositorio recetaRepositorio;
    @Autowired
    private PlanRecetaRecetaRepositorio planrecetaRecetaRepositorio;

    public Planreceta crearPlanReceta(Integer idPlanAlimenticio) {
        Planalimenticio plan = planAlimenticioRepositorio.findById(idPlanAlimenticio)
                .orElseThrow(() -> new RuntimeException("No existe el plan alimenticio con ID: " + idPlanAlimenticio));
        /** opcion 1
        // Verificar si ya existe un planreceta para ese plan alimenticio
        //Planreceta planreceta = planRecetaRepositorio.findByIdplanalimenticio(plan);


         (planreceta == null) {
            planreceta = new Planreceta();
            planreceta.setIdplanalimenticio(plan);
            planreceta.setFecharegistro(LocalDate.now());
            planreceta.setFavorito(false);
            planreceta = planRecetaRepositorio.save(planreceta);
        }*/
        // opcion 2--funciona con el editar
        Planreceta planreceta = new Planreceta();
        planreceta.setIdplanalimenticio(plan);
        planreceta.setFecharegistro(LocalDate.now());
        planreceta.setFavorito(false);
        planreceta = planRecetaRepositorio.save(planreceta);


        return planreceta;
    }

    public String asignarRecetasAPlan(Integer idPlanReceta) {
        // Mantenemos este método para la carga inicial o manual,
        // pero NO lo usaremos para regenerar el plan cada vez que se lista.
        Planreceta planreceta = planRecetaRepositorio.findById(idPlanReceta)
                .orElseThrow(() -> new RuntimeException("No existe el plan receta con ID: " + idPlanReceta));

        Planalimenticio plan = planreceta.getIdplanalimenticio();
        double caloriasObjetivo = plan.getCaloriasDiaria();

        List<Receta> todasRecetas = recetaRepositorio.findAll();
        List<PlanRecetaReceta> relacionesActuales = planrecetaRecetaRepositorio.findByPlanreceta(planreceta);
        Set<Integer> recetasActualesIds = relacionesActuales.stream()
                .map(rel -> rel.getReceta().getId())
                .collect(Collectors.toSet());

        // Solo lógica de llenado inicial (si está vacío o se pide recalcular)
        for (Receta receta : todasRecetas) {
            double cal = Optional.ofNullable(receta.getCalorias()).orElse(0.0);
            // Aquí puedes mantener la lógica simple de asignación
            // ya que el FILTRADO real ocurrirá al listar.
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

    // ✅ LÓGICA COMPLETA: Filtrado por TODOS los macronutrientes
    @Override
    public List<PlanRecetaDTO> listarPorPaciente(Integer idPaciente) {
        // 1. Obtener el plan asignado
        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);

        List<Planreceta> unicos = planes.stream()
                .collect(Collectors.toMap(
                        p -> p.getIdplanalimenticio().getId(),
                        p -> p,
                        (p1, p2) -> p1.getFecharegistro().isAfter(p2.getFecharegistro()) ? p1 : p2
                ))
                .values()
                .stream()
                .collect(Collectors.toList());

        // 2. Calcular consumos de HOY para TODOS los macros
        LocalDate hoy = LocalDate.now();
        List<Seguimiento> consumosHoy = seguimientoRepositorio.buscarPorPacienteYFecha(idPaciente, hoy);

        double conCal = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCalorias()).orElse(0.0)).sum();
        double conPro = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getProteinas()).orElse(0.0)).sum();
        double conGra = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getGrasas()).orElse(0.0)).sum();
        double conCar = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCarbohidratos()).orElse(0.0)).sum();

        return unicos.stream().map(planReceta -> {
            PlanRecetaDTO dto = modelMapper.map(planReceta, PlanRecetaDTO.class);
            Planalimenticio planAlim = planReceta.getIdplanalimenticio();

            // 3. Calcular SALDOS DISPONIBLES (Objetivo - Consumido)
            double salCal = Optional.ofNullable(planAlim.getCaloriasDiaria()).orElse(2000.0) - conCal;
            double salPro = Optional.ofNullable(planAlim.getProteinasDiaria()).orElse(0.0) - conPro;
            double salGra = Optional.ofNullable(planAlim.getGrasasDiaria()).orElse(0.0) - conGra;
            double salCar = Optional.ofNullable(planAlim.getCarbohidratosDiaria()).orElse(0.0) - conCar;

            // Evitar negativos
            if (salCal < 0) salCal = 0;
            if (salPro < 0) salPro = 0;
            if (salGra < 0) salGra = 0;
            if (salCar < 0) salCar = 0;

            // Variables finales para el lambda
            final double fSalCal = salCal;
            final double fSalPro = salPro;
            final double fSalGra = salGra;
            final double fSalCar = salCar;

            List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planReceta);

            // Lógica Plan Free
            String tipoPlan = planReceta.getIdplanalimenticio().getIdpaciente().getIdplan().getTipo();
            if ("Plan free".equalsIgnoreCase(tipoPlan) && relaciones.size() > 15) {
                relaciones = relaciones.subList(0, 15);
            }

            // 4. FILTRADO ESTRICTO: La receta debe caber en TODO
            List<PlanRecetaRecetaDTO> relacionesDTO = relaciones.stream()
                    .filter(rel -> {
                        Receta r = rel.getReceta();
                        double rCal = Optional.ofNullable(r.getCalorias()).orElse(0.0);
                        double rPro = Optional.ofNullable(r.getProteinas()).orElse(0.0);
                        double rGra = Optional.ofNullable(r.getGrasas()).orElse(0.0);
                        double rCar = Optional.ofNullable(r.getCarbohidratos()).orElse(0.0);

                        // CONDICIÓN MAESTRA: Debe cumplir con los 4 requisitos
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
            return dto;
        }).collect(Collectors.toList());
    }

    // ✅ ACTUALIZACIÓN TAMBIÉN PARA LA BÚSQUEDA POR HORARIO
    @Override
    public List<RecetaDTO> listarRecetasPorHorarioEnPlanRecienteDePaciente(Integer idPaciente, String nombreHorario) {
        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);
        if (planes.isEmpty()) return Collections.emptyList();

        Planreceta planreceta = planes.stream()
                .max(Comparator.comparing(Planreceta::getFecharegistro))
                .orElseThrow(() -> new RuntimeException("No hay plan receta"));

        // 1. Calcular consumos
        LocalDate hoy = LocalDate.now();
        List<Seguimiento> consumosHoy = seguimientoRepositorio.buscarPorPacienteYFecha(idPaciente, hoy);

        double conCal = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCalorias()).orElse(0.0)).sum();
        double conPro = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getProteinas()).orElse(0.0)).sum();
        double conGra = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getGrasas()).orElse(0.0)).sum();
        double conCar = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCarbohidratos()).orElse(0.0)).sum();

        // 2. Calcular saldos
        Planalimenticio planAlim = planreceta.getIdplanalimenticio();
        double salCal = Math.max(0, Optional.ofNullable(planAlim.getCaloriasDiaria()).orElse(0.0) - conCal);
        double salPro = Math.max(0, Optional.ofNullable(planAlim.getProteinasDiaria()).orElse(0.0) - conPro);
        double salGra = Math.max(0, Optional.ofNullable(planAlim.getGrasasDiaria()).orElse(0.0) - conGra);
        double salCar = Math.max(0, Optional.ofNullable(planAlim.getCarbohidratosDiaria()).orElse(0.0) - conCar);

        final double fSalCal = salCal;
        final double fSalPro = salPro;
        final double fSalGra = salGra;
        final double fSalCar = salCar;

        // 3. Filtrar
        List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planreceta);
        return relaciones.stream()
                .map(PlanRecetaReceta::getReceta)
                .filter(receta -> receta.getIdhorario().getNombre().equalsIgnoreCase(nombreHorario))
                .filter(r -> {
                    double rCal = Optional.ofNullable(r.getCalorias()).orElse(0.0);
                    double rPro = Optional.ofNullable(r.getProteinas()).orElse(0.0);
                    double rGra = Optional.ofNullable(r.getGrasas()).orElse(0.0);
                    double rCar = Optional.ofNullable(r.getCarbohidratos()).orElse(0.0);

                    // Solo pasa si cabe en TODOS los macronutrientes
                    return rCal <= fSalCal && rPro <= fSalPro && rGra <= fSalGra && rCar <= fSalCar;
                })
                .map(receta -> modelMapper.map(receta, RecetaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    // Autocompletar nombres de recetas dentro del plan receta más reciente del paciente
    public List<String> autocompletarNombreRecetaEnPlanReciente(Integer idPaciente, String texto) {
        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);
        if (planes.isEmpty()) return Collections.emptyList();
        Planreceta planreceta = planes.stream()
                .max(Comparator.comparing(Planreceta::getFecharegistro))
                .orElseThrow(() -> new RuntimeException("No hay plan receta para el paciente"));

        List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planreceta);
        return relaciones.stream()
                .map(PlanRecetaReceta::getReceta)
                .map(Receta::getNombre)
                .filter(nombre -> nombre != null && nombre.toLowerCase().contains(texto.toLowerCase()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    // Buscar recetas por múltiples campos dentro del plan receta más reciente del paciente
    public List<RecetaDTO> buscarRecetasEnPlanReciente(Integer idPaciente, String texto) {
        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);
        if (planes.isEmpty()) return Collections.emptyList();
        Planreceta planreceta = planes.stream()
                .max(Comparator.comparing(Planreceta::getFecharegistro))
                .orElseThrow(() -> new RuntimeException("No hay plan receta para el paciente"));

        String textoLower = texto.toLowerCase();
        List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planreceta);
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
    @Override
    public List<Map<String, Object>> listarRecetasAgregadasHoyPorPacienteId(Integer pacienteId) { // 👈 Cambio a Object
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

        // No usamos Set<String> vistos para evitar ocultar duplicados si comiste lo mismo 2 veces
        // Si quieres permitir borrar individualmente, necesitamos listar todos.

        for (Seguimiento s : seguimientos) {
            if (s == null || s.getPlanRecetaReceta() == null) continue;
            Receta receta = s.getPlanRecetaReceta().getReceta();

            if (receta != null) {
                Map<String, Object> m = new LinkedHashMap<>();
                // ✅ AGREGAMOS LOS IDS NECESARIOS PARA ELIMINAR
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
        // 1. Obtener los planes del paciente
        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);
        if (planes == null || planes.isEmpty()) return Collections.emptyList();

        // Quedarnos con los planes más recientes
        List<Planreceta> unicos = planes.stream()
                .collect(Collectors.toMap(
                        p -> p.getIdplanalimenticio().getId(),
                        p -> p,
                        (p1, p2) -> p1.getFecharegistro().isAfter(p2.getFecharegistro()) ? p1 : p2
                )).values().stream().collect(Collectors.toList());

        // 2. Calcular consumos de HOY (Igual que en los otros métodos)
        LocalDate hoy = LocalDate.now();
        List<Seguimiento> consumosHoy = seguimientoRepositorio.buscarPorPacienteYFecha(idPaciente, hoy);

        double conCal = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCalorias()).orElse(0.0)).sum();
        double conPro = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getProteinas()).orElse(0.0)).sum();
        double conGra = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getGrasas()).orElse(0.0)).sum();
        double conCar = consumosHoy.stream().mapToDouble(s -> Optional.ofNullable(s.getCarbohidratos()).orElse(0.0)).sum();

        return unicos.stream().map(planReceta -> {
            Planalimenticio planAlim = planReceta.getIdplanalimenticio();

            // 3. Calcular SALDOS DISPONIBLES (Objetivo - Consumido)
            double salCal = Math.max(0, Optional.ofNullable(planAlim.getCaloriasDiaria()).orElse(2000.0) - conCal);
            double salPro = Math.max(0, Optional.ofNullable(planAlim.getProteinasDiaria()).orElse(0.0) - conPro);
            double salGra = Math.max(0, Optional.ofNullable(planAlim.getGrasasDiaria()).orElse(0.0) - conGra);
            double salCar = Math.max(0, Optional.ofNullable(planAlim.getCarbohidratosDiaria()).orElse(0.0) - conCar);

            // Variables finales para usar dentro del stream
            final double fSalCal = salCal;
            final double fSalPro = salPro;
            final double fSalGra = salGra;
            final double fSalCar = salCar;

            List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planReceta);

            List<PlanRecetaRecetaDTO> favs = relaciones.stream()
                    // Filtro 1: Solo Favoritos
                    .filter(rel -> Boolean.TRUE.equals(rel.getFavorito()))
                    // Filtro 2: Que quepan en el saldo nutricional restante (Igual que en la lista general)
                    .filter(rel -> {
                        Receta r = rel.getReceta();
                        double rCal = Optional.ofNullable(r.getCalorias()).orElse(0.0);
                        double rPro = Optional.ofNullable(r.getProteinas()).orElse(0.0);
                        double rGra = Optional.ofNullable(r.getGrasas()).orElse(0.0);
                        double rCar = Optional.ofNullable(r.getCarbohidratos()).orElse(0.0);

                        // La receta solo pasa si es menor o igual a lo que sobra de TODOS los macros
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

            // Si después de filtrar no queda nada (porque ya comiste todo), no devolvemos el plan vacío
            if (favs.isEmpty()) return null;

            PlanRecetaDTO dto = modelMapper.map(planReceta, PlanRecetaDTO.class);
            dto.setRecetas(favs);
            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
    // ✅ REEMPLAZA TU MÉTODO actualizarFavorito ACTUAL POR ESTE:
    @Override
    public PlanRecetaRecetaDTO actualizarFavorito(Long idPlanRecetaReceta, Boolean favorito) {
        // 1. Buscamos la relación específica (Plato específico dentro del Plan)
        PlanRecetaReceta relacion = planrecetaRecetaRepositorio.findById(idPlanRecetaReceta)
                .orElseThrow(() -> new RuntimeException("No existe la relación con ID: " + idPlanRecetaReceta));

        // 2. Guardamos el favorito EN LA RECETA, no en el plan
        relacion.setFavorito(Boolean.TRUE.equals(favorito));
        PlanRecetaReceta actualizada = planrecetaRecetaRepositorio.save(relacion);

        // 3. Convertimos manualmente a DTO para devolverlo
        PlanRecetaRecetaDTO dto = new PlanRecetaRecetaDTO();
        dto.setIdPlanRecetaReceta(actualizada.getIdPlanRecetaReceta());
        dto.setIdPlanReceta(actualizada.getPlanreceta().getId());
        dto.setFavorito(actualizada.getFavorito());

        // Mapeamos la receta interior
        RecetaDTO recetaDTO = modelMapper.map(actualizada.getReceta(), RecetaDTO.class);
        dto.setRecetaDTO(recetaDTO);

        return dto;
    }



}
