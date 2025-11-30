package com.upc.vitalco.services;

import com.upc.vitalco.dto.*;
import com.upc.vitalco.entidades.*;
import com.upc.vitalco.interfaces.ISeguimientoServices;
import com.upc.vitalco.repositorios.*;
import jakarta.transaction.Transactional;
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
    private PlanRecetaRecetaRepositorio planRecetaRecetaRepositorio;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private PlanAlimenticioRepositorio planAlimenticioRepositorio;

    @Autowired
    private CitaRepositorio citaRepositorio;


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
        Planalimenticio planAli = plan.getIdplanalimenticio();
        if (planAli == null) {
            throw new RuntimeException("El plan asociado no contiene un Planalimenticio");
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
        seguimiento.setObjetivoSnapshot(planAli != null ? planAli.getIdpaciente().getIdPlanNutricional().getObjetivo() : null);

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
   //corregir listar por dia solo debe visualizarse totales nutricionles por horario, total de caloria, totales nutricionales
   //probar y eliminar esto, resumen seguimiento
   @Override
   public SeguimientoResumenDTO listarPorDia(Integer pacienteId, LocalDate fecha) {
       if (pacienteId == null || pacienteId <= 0) {
           throw new IllegalArgumentException("pacienteId inválido");
       }
       if (fecha == null) {
           throw new IllegalArgumentException("fecha no puede ser null");
       }

       // Verificar existencia de paciente
       pacienteRepositorio.findById(pacienteId)
               .orElseThrow(() -> new RuntimeException("Paciente con id " + pacienteId + " no encontrado"));

       Map<String, Double> totales = Optional.ofNullable(obtenerTotalesNutricionales(pacienteId, fecha))
               .orElseGet(HashMap::new);
       Map<String, Double> caloriasPorHorario = Optional.ofNullable(listarCaloriasPorHorario(pacienteId, fecha))
               .orElseGet(HashMap::new);

       SeguimientoResumenDTO resumen = new SeguimientoResumenDTO();
       resumen.setTotalesNutricionales(totales);
       resumen.setCaloriasPorHorario(caloriasPorHorario);

       return resumen;
   }


    public Map<String, Double> obtenerTotalesNutricionales(Integer pacienteId, LocalDate fecha) {
        // 1. Calcular lo consumido en esa fecha
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

        // 2. ✅ Buscar el plan que estaba vigente EN ESA FECHA
        Optional<Planalimenticio> planHistorico = planAlimenticioRepositorio.buscarPlanEnFecha(pacienteId, fecha);

        // 3. Valores por defecto seguros (por si no hay plan)
        double reqCalorias = 2000.0;
        double reqCarbohidratos = 250.0;
        double reqProteinas = 100.0;
        double reqGrasas = 70.0;

        if (planHistorico.isPresent()) {
            Planalimenticio plan = planHistorico.get();

            // ✅ LOG CRÍTICO para debugging
            System.out.println("═══════════════════════════════════════════");
            System.out.println("📅 Buscando plan para fecha: " + fecha);
            System.out.println("✅ Plan encontrado:");
            System.out.println("   ID Plan: " + plan.getId());
            System.out.println("   Fecha Creación: " + plan.getFechaCreacion());
            System.out.println("   Calorías: " + plan.getCaloriasDiaria());
            System.out.println("   Proteínas: " + plan.getProteinasDiaria());
            System.out.println("   Grasas: " + plan.getGrasasDiaria());
            System.out.println("   Carbohidratos: " + plan.getCarbohidratosDiaria());
            System.out.println("═══════════════════════════════════════════");

            reqCalorias = Optional.ofNullable(plan.getCaloriasDiaria()).orElse(reqCalorias);
            reqCarbohidratos = Optional.ofNullable(plan.getCarbohidratosDiaria()).orElse(reqCarbohidratos);
            reqProteinas = Optional.ofNullable(plan.getProteinasDiaria()).orElse(reqProteinas);
            reqGrasas = Optional.ofNullable(plan.getGrasasDiaria()).orElse(reqGrasas);
        } else {
            System.out.println("⚠️ NO se encontró plan para fecha: " + fecha + " paciente: " + pacienteId);
            System.out.println("   Usando valores por defecto");

            // Buscar TODOS los planes para debug
            List<Planalimenticio> todosPlanes = planAlimenticioRepositorio.buscarPorPaciente(pacienteId);
            System.out.println("   Planes disponibles:");
            todosPlanes.forEach(p ->
                    System.out.println("      - ID: " + p.getId() + ", Fecha: " + p.getFechaCreacion())
            );
        }

        Map<String, Double> totales = new HashMap<>();
        totales.put("calorias", totalCalorias);
        totales.put("carbohidratos", totalCarbohidratos);
        totales.put("proteinas", totalProteinas);
        totales.put("grasas", totalGrasas);
        totales.put("requerido_calorias", reqCalorias);
        totales.put("requerido_carbohidratos", reqCarbohidratos);
        totales.put("requerido_proteinas", reqProteinas);
        totales.put("requerido_grasas", reqGrasas);

        return totales;
    }

    public Map<String, Double> listarCaloriasPorHorario(Integer pacienteId, LocalDate fecha) {
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);

        Map<String, Double> caloriasPorHorario = new HashMap<>();

        seguimientos.stream()
                .filter(s -> s.getId() != null) // Solo seguimientos activos
                .forEach(s -> {
                    Receta receta = s.getPlanRecetaReceta().getReceta();
                    if (receta != null && receta.getIdhorario() != null) {
                        String horario = receta.getIdhorario().getNombre().toLowerCase();
                        double calorias = Optional.ofNullable(receta.getCalorias()).orElse(0.0);
                        caloriasPorHorario.merge(horario, calorias, Double::sum);
                    }
                });

        for (String h : List.of("desayuno", "snack", "almuerzo", "cena")) {
            caloriasPorHorario.putIfAbsent(h, 0.0);
        }

        return caloriasPorHorario;
    }


    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    @Override
    public Map<String, Object> verificarCumplimientoDiario(String dni, LocalDate fecha) {
        Paciente paciente = pacienteRepositorio.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // ✅ VALIDACIÓN: Verificar que existe cita aceptada
        boolean tieneCitaAceptada = citaRepositorio.existsByPacienteIdAndEstado(paciente.getId(), "Aceptada");
        if (!tieneCitaAceptada) {
            throw new RuntimeException("No tienes citas aceptadas con este paciente");
        }

        // a) Consumido
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(paciente.getId(), fecha);
        double conCal = seguimientos.stream().mapToDouble(s -> s.getCalorias() != null ? s.getCalorias() : 0).sum();
        double conProt = seguimientos.stream().mapToDouble(s -> s.getProteinas() != null ? s.getProteinas() : 0).sum();
        double conGrasas = seguimientos.stream().mapToDouble(s -> s.getGrasas() != null ? s.getGrasas() : 0).sum();
        double conCarb = seguimientos.stream().mapToDouble(s -> s.getCarbohidratos() != null ? s.getCarbohidratos() : 0).sum();

        // b) Requerido (Meta Histórica)
        Optional<Planalimenticio> planHistorico = planAlimenticioRepositorio.buscarPlanEnFecha(paciente.getId(), fecha);

        double reqCal = 2000, reqProt = 100, reqGrasas = 50, reqCarb = 200;

        if (planHistorico.isPresent()) {
            Planalimenticio plan = planHistorico.get();
            reqCal = plan.getCaloriasDiaria() != null ? plan.getCaloriasDiaria() : reqCal;
            reqProt = plan.getProteinasDiaria() != null ? plan.getProteinasDiaria() : reqProt;
            reqGrasas = plan.getGrasasDiaria() != null ? plan.getGrasasDiaria() : reqGrasas;
            reqCarb = plan.getCarbohidratosDiaria() != null ? plan.getCarbohidratosDiaria() : reqCarb;
        }

        // c) Armar Respuesta
        Map<String, Object> resp = new HashMap<>();
        resp.put("calorias", crearInfo(conCal, reqCal));
        resp.put("proteinas", crearInfo(conProt, reqProt));
        resp.put("grasas", crearInfo(conGrasas, reqGrasas));
        resp.put("carbohidratos", crearInfo(conCarb, reqCarb));

        return resp;
    }

    private Map<String, Object> crearInfo(double consumido, double requerido) {
        double porcentaje = (requerido > 0) ? (consumido / requerido) * 100 : 0;
        return Map.of("consumido", consumido, "requerido", requerido, "porcentaje", porcentaje);
    }

    @Override
    public void eliminarRecetaDeSeguimiento(Integer pacienteId, Integer seguimientoId, Integer recetaId) {
        // 1. Buscamos el seguimiento
        Seguimiento seguimiento = seguimientoRepositorio.findById(seguimientoId)
                .orElseThrow(() -> new RuntimeException("Seguimiento no encontrado"));

        // 2. Validaciones de seguridad
        // Verificar que el seguimiento pertenezca al paciente (a través del plan)
        Integer idPacienteDelSeguimiento = seguimiento.getPlanRecetaReceta()
                .getPlanreceta().getIdplanalimenticio().getIdpaciente().getId();

        if (!idPacienteDelSeguimiento.equals(pacienteId)) {
            throw new RuntimeException("El seguimiento no corresponde al paciente");
        }

        LocalDate hoy = LocalDate.now();
        if (!seguimiento.getFecharegistro().equals(hoy)) {
            throw new RuntimeException("Solo se pueden eliminar recetas registradas en el día actual");
        }

        // 3. ✅ BORRADO SEGURO: Usar el método estándar de JPA
        seguimientoRepositorio.delete(seguimiento);
    }
    //metodo de nutricionista, paciente
    @Override
    public SeguimientoResumenDTO resumenSeguimientoNutri(String dni, LocalDate fecha) {
        Paciente paciente = pacienteRepositorio.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI " + dni + " no encontrado"));

        boolean tieneCitaAceptada = citaRepositorio.existsByPacienteIdAndEstado(paciente.getId(), "Aceptada");
        if (!tieneCitaAceptada) {
            throw new RuntimeException("El paciente no tiene citas aceptadas. Solo se puede ver el progreso de pacientes con citas aceptadas");
        }

        // Obtener el usuario asociado al paciente
        Usuario usuario = paciente.getIdusuario();
        String nombreUsuario = usuario != null ? usuario.getNombre() : "";
        Map<String, Double> totalesNutricionales = obtenerTotalesNutricionales(paciente.getId(), fecha);
        Map<String, Double> caloriasPorHorario = listarCaloriasPorHorario(paciente.getId(), fecha);

        SeguimientoResumenDTO resumen = new SeguimientoResumenDTO();
        resumen.setNombrePaciente(nombreUsuario);
        resumen.setTotalesNutricionales(totalesNutricionales);
        resumen.setCaloriasPorHorario(caloriasPorHorario);

        return resumen;
    }
    //nutri modificar
    @Override
    public List<SeguimientoBusquedaDTO> listarPorDniYFecha(String dni, LocalDate fecha) {
        // Buscar el paciente por DNI
        Paciente paciente = pacienteRepositorio.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI " + dni + " no encontrado"));

        // Verificar que existe al menos una cita ACEPTADA para este paciente
        boolean tieneCitaAceptada = citaRepositorio.existsByPacienteIdAndEstado(paciente.getId(), "Aceptada");
        if (!tieneCitaAceptada) {
            throw new RuntimeException("El paciente no tiene citas aceptadas. Solo se puede ver el progreso de pacientes con citas aceptadas");
        }
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(paciente.getId(), fecha);
        if (seguimientos == null || seguimientos.isEmpty()) {
            throw new RuntimeException("No hay seguimientos para la fecha indicada");
        }

        Seguimiento seleccionado = seguimientos.stream()
                .max(Comparator.comparing(Seguimiento::getId))
                .orElseThrow(() -> new RuntimeException("No se pudo determinar el seguimiento más reciente"));

        SeguimientoBusquedaDTO resp = new SeguimientoBusquedaDTO();
        resp.setApellidoPaciente(paciente.getIdusuario() != null ? paciente.getIdusuario().getApellido() : "");
        resp.setNombrePaciente(paciente.getIdusuario() != null ? paciente.getIdusuario().getNombre() : "");
        resp.setObjetivoPaciente(seleccionado.getObjetivoSnapshot() != null
                ? seleccionado.getObjetivoSnapshot()
                : (paciente.getIdPlanNutricional() != null ? paciente.getIdPlanNutricional().getObjetivo() : null));

        return Collections.singletonList(resp);
    }

    @Override
    @Transactional
    public List<HistorialSemanalDTO> obtenerHistorialFiltrado(String dni, String objetivo, LocalDate fechaInicio, LocalDate fechaFin) {

        // 1. Traemos TODO el historial sin filtros SQL para evitar el error bytea
        List<Seguimiento> todos = seguimientoRepositorio.buscarHistorialBruto(dni);

        if (todos.isEmpty()) return new ArrayList<>();

        // 2. Filtramos en Java (Esto no falla nunca)
        List<Seguimiento> filtrados = todos.stream()
                .filter(s -> {
                    boolean pasaFecha = true;
                    if (fechaInicio != null && s.getFecharegistro().isBefore(fechaInicio)) pasaFecha = false;
                    if (fechaFin != null && s.getFecharegistro().isAfter(fechaFin)) pasaFecha = false;

                    boolean pasaObjetivo = true;
                    if (objetivo != null && !objetivo.isEmpty()) {
                        String objSnapshot = s.getObjetivoSnapshot(); // Si esto viene raro de la DB, aquí lo manejamos como String
                        if (objSnapshot == null || !objSnapshot.toLowerCase().contains(objetivo.toLowerCase())) {
                            pasaObjetivo = false;
                        }
                    }
                    return pasaFecha && pasaObjetivo;
                })
                .collect(Collectors.toList());

        // 3. Agrupar y Mapear (Lógica original)
        Map<LocalDate, Double> consumoPorDia = new HashMap<>();
        Map<LocalDate, Double> metaPorDia = new HashMap<>();

        for (Seguimiento s : filtrados) {
            LocalDate f = s.getFecharegistro();
            Double cal = s.getCalorias() != null ? s.getCalorias() : 0.0;

            Double meta = 2000.0;
            try {
                if(s.getPlanRecetaReceta() != null && s.getPlanRecetaReceta().getPlanreceta() != null) {
                    meta = s.getPlanRecetaReceta().getPlanreceta().getIdplanalimenticio().getCaloriasDiaria();
                }
            } catch (Exception e) { }

            consumoPorDia.merge(f, cal, Double::sum);
            metaPorDia.putIfAbsent(f, meta);
        }

        List<HistorialSemanalDTO> historial = new ArrayList<>();
        List<LocalDate> fechas = new ArrayList<>(consumoPorDia.keySet());
        Collections.sort(fechas);

        for (LocalDate f : fechas) {
            historial.add(new HistorialSemanalDTO(f, consumoPorDia.get(f), metaPorDia.get(f)));
        }
        return historial;
    }

    @Override
    public SeguimientoResumenDTO resumenSeguimientoPaciente(String dni, LocalDate fecha) {
        // 1. Buscar paciente
        Paciente paciente = pacienteRepositorio.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente con DNI " + dni + " no encontrado"));

        // 2. Obtener el usuario asociado
        Usuario usuario = paciente.getIdusuario();
        String nombreUsuario = usuario != null ? usuario.getNombre() : "";

        // 3. ✅ AQUÍ ESTÁ LA CLAVE: Obtener totales CON PLAN HISTÓRICO
        // Los métodos obtenerTotalesNutricionales y listarCaloriasPorHorario
        // YA usan buscarPlanEnFecha internamente, por eso funcionan correctamente
        Map<String, Double> totalesNutricionales = obtenerTotalesNutricionales(paciente.getId(), fecha);
        Map<String, Double> caloriasPorHorario = listarCaloriasPorHorario(paciente.getId(), fecha);

        // 4. Construir respuesta
        SeguimientoResumenDTO resumen = new SeguimientoResumenDTO();
        resumen.setNombrePaciente(nombreUsuario);
        resumen.setTotalesNutricionales(totalesNutricionales);
        resumen.setCaloriasPorHorario(caloriasPorHorario);

        return resumen;
    }

}
