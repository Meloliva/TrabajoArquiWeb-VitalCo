package com.upc.vitalco.services;

import com.upc.vitalco.dto.*;
import com.upc.vitalco.entidades.*;
import com.upc.vitalco.interfaces.ISeguimientoServices;
import com.upc.vitalco.repositorios.*;
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
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);

        // Filtrar solo seguimientos activos
        List<Seguimiento> seguimientosActivos = seguimientos.stream()
                .filter(s -> s.getId() != null)
                .collect(Collectors.toList());

        double totalCalorias = seguimientosActivos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getCalorias()).orElse(0.0))
                .sum();

        double totalCarbohidratos = seguimientosActivos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getCarbohidratos()).orElse(0.0))
                .sum();

        double totalProteinas = seguimientosActivos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getProteinas()).orElse(0.0))
                .sum();

        double totalGrasas = seguimientosActivos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getGrasas()).orElse(0.0))
                .sum();

        // Obtener totales requeridos del plan alimenticio
        Planalimenticio plan = planAlimenticioRepositorio.buscarPorPaciente(pacienteId);
        if (plan == null) {
            throw new RuntimeException("El paciente no tiene un plan alimenticio asignado");
        }

        double reqCalorias = Optional.ofNullable(plan.getCaloriasDiaria()).orElse(0.0);
        double reqCarbohidratos = Optional.ofNullable(plan.getCarbohidratosDiaria()).orElse(0.0);
        double reqProteinas = Optional.ofNullable(plan.getProteinasDiaria()).orElse(0.0);
        double reqGrasas = Optional.ofNullable(plan.getGrasasDiaria()).orElse(0.0);

        Map<String, Double> totales = new HashMap<>();
        totales.put("calorias", totalCalorias);
        totales.put("carbohidratos", totalCarbohidratos);
        totales.put("proteinas", totalProteinas);
        totales.put("grasas", totalGrasas);

        // Totales requeridos según plan
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
                .orElseThrow(() -> new RuntimeException("Paciente con DNI " + dni + " no encontrado"));

        // Obtener seguimientos filtrados (sin eliminados)
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(paciente.getId(), fecha)
                .stream()
                .filter(s -> s.getId() != null) // Solo seguimientos activos (no eliminados)
                .collect(Collectors.toList());

        // Calcular totales desde seguimientos filtrados
        double conCal = seguimientos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getCalorias()).orElse(0.0))
                .sum();
        double conProt = seguimientos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getProteinas()).orElse(0.0))
                .sum();
        double conGrasas = seguimientos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getGrasas()).orElse(0.0))
                .sum();
        double conCarb = seguimientos.stream()
                .mapToDouble(s -> Optional.ofNullable(s.getCarbohidratos()).orElse(0.0))
                .sum();

        Planalimenticio plan = planAlimenticioRepositorio.buscarPorPaciente(paciente.getId());
        if (plan == null) {
            throw new RuntimeException("El paciente no tiene un plan alimenticio asignado");
        }

        double reqCal = Optional.ofNullable(plan.getCaloriasDiaria()).orElse(0.0);
        double reqProt = Optional.ofNullable(plan.getProteinasDiaria()).orElse(0.0);
        double reqGrasas = Optional.ofNullable(plan.getGrasasDiaria()).orElse(0.0);
        double reqCarb = Optional.ofNullable(plan.getCarbohidratosDiaria()).orElse(0.0);

        double porcCal = reqCal > 0 ? (conCal / reqCal) * 100 : 0;
        double porcProt = reqProt > 0 ? (conProt / reqProt) * 100 : 0;
        double porcGrasas = reqGrasas > 0 ? (conGrasas / reqGrasas) * 100 : 0;
        double porcCarb = reqCarb > 0 ? (conCarb / reqCarb) * 100 : 0;

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("calorias", Map.of("consumido", conCal, "requerido", reqCal, "porcentaje", redondear(porcCal)));
        resultado.put("proteinas", Map.of("consumido", conProt, "requerido", reqProt, "porcentaje", redondear(porcProt)));
        resultado.put("grasas", Map.of("consumido", conGrasas, "requerido", reqGrasas, "porcentaje", redondear(porcGrasas)));
        resultado.put("carbohidratos", Map.of("consumido", conCarb, "requerido", reqCarb, "porcentaje", redondear(porcCarb)));

        boolean cumplio = porcCal == 100 && porcProt == 100 && porcGrasas == 100 && porcCarb == 100;
        resultado.put("cumplio", cumplio);

        return resultado;
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

}
