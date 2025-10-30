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

        return unicos.stream()
                .map(planReceta -> {
                    PlanRecetaDTO dto = modelMapper.map(planReceta, PlanRecetaDTO.class);

                    List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planReceta);
                    String tipoPlan = planReceta.getIdplanalimenticio().getIdpaciente().getIdplan().getTipo();
                    if ("Plan free".equalsIgnoreCase(tipoPlan) && relaciones.size() > 15) {
                        relaciones = relaciones.subList(0, 15);
                    }
                    List<PlanRecetaRecetaDTO> relacionesDTO = relaciones.stream()
                            .map(rel -> {
                                PlanRecetaRecetaDTO relDTO = new PlanRecetaRecetaDTO();
                                relDTO.setIdPlanRecetaReceta(rel.getIdPlanRecetaReceta());
                                relDTO.setIdPlanReceta(rel.getPlanreceta().getId());

                                RecetaDTO recetaDTO = modelMapper.map(rel.getReceta(), RecetaDTO.class);
                                relDTO.setRecetaDTO(recetaDTO);

                                return relDTO;
                            })
                            .collect(Collectors.toList());

                    dto.setRecetas(relacionesDTO);
                    return dto;
                })
                .collect(Collectors.toList());
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
    public List<Map<String, String>> listarRecetasAgregadasHoyPorPacienteId(Integer pacienteId) {
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

        // Mantener orden y eliminar duplicados por nombre
        Set<String> vistos = new LinkedHashSet<>();
        List<Map<String, String>> resultado = new ArrayList<>();
        for (Seguimiento s : seguimientos) {
            if (s == null || s.getPlanRecetaReceta() == null) continue;
            Receta receta = s.getPlanRecetaReceta().getReceta();
            if (receta == null || receta.getNombre() == null) continue;
            String nombre = receta.getNombre();
            if (vistos.add(nombre)) { // solo la primera aparición se añade
                Map<String, String> m = new LinkedHashMap<>();
                m.put("nombre", nombre);
                m.put("descripcion", Optional.ofNullable(receta.getDescripcion()).orElse(""));
                resultado.add(m);
            }
        }

        return resultado;
    }

    public List<PlanRecetaDTO> listarFavoritosPorPaciente(Integer idPaciente) {
        if (idPaciente == null) {
            throw new IllegalArgumentException("id de paciente inválido");
        }

        List<Planreceta> planes = planRecetaRepositorio.buscarPorPaciente(idPaciente);
        if (planes == null || planes.isEmpty()) {
            return Collections.emptyList();
        }

        // Filtrar solo favoritos
        List<Planreceta> favoritos = planes.stream()
                .filter(p -> Boolean.TRUE.equals(p.getFavorito()))
                .collect(Collectors.toList());
        if (favoritos.isEmpty()) return Collections.emptyList();

        // Tomar el plan más reciente (último creado) por cada plan alimenticio
        List<Planreceta> unicos = favoritos.stream()
                .collect(Collectors.toMap(
                        p -> p.getIdplanalimenticio().getId(),
                        p -> p,
                        (p1, p2) -> p1.getFecharegistro().isAfter(p2.getFecharegistro()) ? p1 : p2
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

        return unicos.stream()
                .map(planReceta -> {
                    PlanRecetaDTO dto = modelMapper.map(planReceta, PlanRecetaDTO.class);

                    List<PlanRecetaReceta> relaciones = planrecetaRecetaRepositorio.findByPlanreceta(planReceta);
                    String tipoPlan = planReceta.getIdplanalimenticio().getIdpaciente().getIdplan().getTipo();
                    if ("Plan free".equalsIgnoreCase(tipoPlan) && relaciones.size() > 15) {
                        relaciones = relaciones.subList(0, 15);
                    }
                    List<PlanRecetaRecetaDTO> relacionesDTO = relaciones.stream()
                            .map(rel -> {
                                PlanRecetaRecetaDTO relDTO = new PlanRecetaRecetaDTO();
                                relDTO.setIdPlanRecetaReceta(rel.getIdPlanRecetaReceta());
                                relDTO.setIdPlanReceta(rel.getPlanreceta().getId());

                                RecetaDTO recetaDTO = modelMapper.map(rel.getReceta(), RecetaDTO.class);
                                relDTO.setRecetaDTO(recetaDTO);

                                return relDTO;
                            })
                            .collect(Collectors.toList());

                    dto.setRecetas(relacionesDTO);
                    return dto;
                })
                .collect(Collectors.toList());
    }


}
