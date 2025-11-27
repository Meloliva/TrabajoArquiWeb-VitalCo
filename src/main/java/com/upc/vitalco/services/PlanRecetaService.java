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
        Planreceta planreceta = planRecetaRepositorio.findById(idPlanReceta)
                .orElseThrow(() -> new RuntimeException("No existe el plan receta con ID: " + idPlanReceta));

        Planalimenticio plan = planreceta.getIdplanalimenticio();

        double caloriasObjetivo = plan.getCaloriasDiaria();
        double proteinasObjetivo = plan.getProteinasDiaria();
        double grasasObjetivo = plan.getGrasasDiaria();
        double carbohidratosObjetivo = plan.getCarbohidratosDiaria();

        List<Receta> todasRecetas = recetaRepositorio.findAll();
        List<PlanRecetaReceta> relacionesActuales = planrecetaRecetaRepositorio.findByPlanreceta(planreceta);
        Set<Integer> recetasActualesIds = relacionesActuales.stream()
                .map(rel -> rel.getReceta().getId())
                .collect(Collectors.toSet());

        for (Receta receta : todasRecetas) {
            double cal = receta.getCalorias() != null ? receta.getCalorias() : 0.0;
            double pro = receta.getProteinas() != null ? receta.getProteinas() : 0.0;
            double gra = receta.getGrasas() != null ? receta.getGrasas() : 0.0;
            double car = receta.getCarbohidratos() != null ? receta.getCarbohidratos() : 0.0;

            boolean cumple = cal <= caloriasObjetivo &&
                    pro <= proteinasObjetivo &&
                    gra <= grasasObjetivo &&
                    car <= carbohidratosObjetivo;

            if (cumple && !recetasActualesIds.contains(receta.getId())) {
                PlanRecetaReceta nuevaRelacion = new PlanRecetaReceta();
                nuevaRelacion.setPlanreceta(planreceta);
                nuevaRelacion.setReceta(receta);
                planrecetaRecetaRepositorio.save(nuevaRelacion);
            }
        }

        return "Recetas asignadas correctamente al plan.";
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

    @Override
    public List<PlanRecetaDTO> listarPorPaciente(Integer idPaciente) {
        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);

        // Tomar el plan más reciente (último creado) por cada plan alimenticio
        List<Planreceta> unicos = planes.stream()
                .collect(Collectors.toMap(
                        p -> p.getIdplanalimenticio().getId(),
                        p -> p,
                        (p1, p2) -> p1.getFecharegistro().isAfter(p2.getFecharegistro()) ? p1 : p2  // ← Tomar el más reciente
                ))
                .values()
                .stream()
                .collect(Collectors.toList());

        for (int i = 0; i < unicos.size(); i++) {
            Planreceta planreceta = unicos.get(i);

            if (planreceta.getId() == null) {
                planreceta = planRecetaRepositorio.save(planreceta);
                unicos.set(i, planreceta);
            }

            asignarRecetasAPlan(planreceta.getId());
        }

        return unicos.stream().map(planReceta -> {
            PlanRecetaDTO dto = modelMapper.map(planReceta, PlanRecetaDTO.class);

            // Obtenemos las recetas de ESTE plan
            List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planReceta);

            // Lógica Plan Free
            String tipoPlan = planReceta.getIdplanalimenticio().getIdpaciente().getIdplan().getTipo();
            if ("Plan free".equalsIgnoreCase(tipoPlan) && relaciones.size() > 15) {
                relaciones = relaciones.subList(0, 15);
            }

            List<PlanRecetaRecetaDTO> relacionesDTO = relaciones.stream().map(rel -> {
                PlanRecetaRecetaDTO relDTO = new PlanRecetaRecetaDTO();
                relDTO.setIdPlanRecetaReceta(rel.getIdPlanRecetaReceta());
                relDTO.setIdPlanReceta(rel.getPlanreceta().getId());

                // ✅ IMPORTANTE: Mapeamos el favorito individual de la tabla intermedia
                relDTO.setFavorito(rel.getFavorito());

                relDTO.setRecetaDTO(modelMapper.map(rel.getReceta(), RecetaDTO.class));
                return relDTO;
            }).collect(Collectors.toList());

            dto.setRecetas(relacionesDTO);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RecetaDTO> listarRecetasPorHorarioEnPlanRecienteDePaciente(Integer idPaciente, String nombreHorario) {

        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);
        if (planes.isEmpty()) {
            return Collections.emptyList();
        }
        Planreceta planreceta = planes.stream()
                .max(Comparator.comparing(Planreceta::getFecharegistro))
                .orElseThrow(() -> new RuntimeException("No hay plan receta para el paciente"));

        List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planreceta);
        return relaciones.stream()
                .map(PlanRecetaReceta::getReceta)
                .filter(receta -> receta.getIdhorario().getNombre().equalsIgnoreCase(nombreHorario))
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
        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);
        if (planes == null || planes.isEmpty()) return Collections.emptyList();

        // Filtramos los planes más recientes
        List<Planreceta> unicos = planes.stream()
                .collect(Collectors.toMap(
                        p -> p.getIdplanalimenticio().getId(),
                        p -> p,
                        (p1, p2) -> p1.getFecharegistro().isAfter(p2.getFecharegistro()) ? p1 : p2
                )).values().stream().collect(Collectors.toList());

        return unicos.stream().map(plan -> {
                    List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(plan);

                    // ✅ FILTRAR: Solo quedarnos con las recetas que tienen favorito = true
                    List<PlanRecetaRecetaDTO> favs = relaciones.stream()
                            .filter(rel -> Boolean.TRUE.equals(rel.getFavorito()))
                            .map(rel -> {
                                PlanRecetaRecetaDTO relDTO = new PlanRecetaRecetaDTO();
                                relDTO.setIdPlanRecetaReceta(rel.getIdPlanRecetaReceta());
                                relDTO.setIdPlanReceta(rel.getPlanreceta().getId());
                                relDTO.setFavorito(true);
                                relDTO.setRecetaDTO(modelMapper.map(rel.getReceta(), RecetaDTO.class));
                                return relDTO;
                            }).collect(Collectors.toList());

                    if (favs.isEmpty()) return null; // Si el plan no tiene recetas favoritas, lo ignoramos

                    PlanRecetaDTO dto = modelMapper.map(plan, PlanRecetaDTO.class);
                    dto.setRecetas(favs);
                    return dto;
                })
                .filter(Objects::nonNull) // Eliminamos los planes nulos (sin favoritos)
                .collect(Collectors.toList());
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
