package com.upc.vitalco.services;

import com.upc.vitalco.dto.NutricionistaRequerimientoDTO;
import com.upc.vitalco.dto.PlanAlimenticioDTO;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.entidades.Planalimenticio;
import com.upc.vitalco.entidades.Plannutricional;
import com.upc.vitalco.entidades.Seguimiento;
import com.upc.vitalco.interfaces.IPlanAlimenticioServices;
import com.upc.vitalco.repositorios.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PlanAlimenticioService implements IPlanAlimenticioServices {
    @Autowired
    private PlanAlimenticioRepositorio planAlimenticioRepositorio;
    @Autowired
    private PacienteRepositorio pacienteRepositorio;
    @Autowired
    private PlanNutricionalRepositorio planNutricionalRepositorio;
    @Autowired
    private PlanRecetaService planRecetaService;

    @Autowired
    private CitaRepositorio citaRepositorio;
    @Autowired
    private SeguimientoService seguimientoService;
    @Autowired
    private SeguimientoRepositorio seguimientoRepositorio;
    @Autowired
    private ModelMapper modelMapper;

    public PlanAlimenticioDTO registrar(Integer idPaciente) {
        Paciente paciente = pacienteRepositorio.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + idPaciente));

        Plannutricional planNutricional = planNutricionalRepositorio.findById(paciente.getIdPlanNutricional().getId())
                .orElseThrow(() -> new RuntimeException("Plan nutricional no encontrado para el paciente con ID: " + idPaciente));

        String objetivo = planNutricional.getObjetivo();
        String duracion = planNutricional.getDuracion();

        double caloriasDiarias = calcularCalorias(paciente, objetivo, duracion);
        double[] macros = calcularMacronutrientes(caloriasDiarias, objetivo, paciente.getTrigliceridos().doubleValue());
        double carbohidratosDiarios = macros[0];
        double proteinasDiarias = macros[1];
        double grasasDiarias = macros[2];

        Planalimenticio planAlimenticio = new Planalimenticio();
        planAlimenticio.setIdpaciente(paciente);
        planAlimenticio.setCaloriasDiaria(caloriasDiarias);
        planAlimenticio.setCarbohidratosDiaria(carbohidratosDiarios);
        planAlimenticio.setProteinasDiaria(proteinasDiarias);
        planAlimenticio.setGrasasDiaria(grasasDiarias);

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFinal = calcularFechaFinal(duracion, fechaInicio);

        planAlimenticio.setFechainicio(fechaInicio);
        planAlimenticio.setFechafin(fechaFinal);

        planAlimenticio = planAlimenticioRepositorio.save(planAlimenticio);
        planRecetaService.crearPlanReceta(planAlimenticio.getId());
        return modelMapper.map(planAlimenticio, PlanAlimenticioDTO.class);
    }

    @Override
    public PlanAlimenticioDTO eliminarPlanAlimenticio(Integer id) {

        if(planAlimenticioRepositorio.existsById(id)) {
            planAlimenticioRepositorio.deleteById(id);
        }
        return null;
    }

    private LocalDate calcularFechaFinal(String duracion, LocalDate fechaInicio) {
        switch (duracion.toLowerCase()) {
            case "3 meses": return fechaInicio.plusMonths(3);
            case "6 meses": return fechaInicio.plusMonths(6);
            case "12 meses": return fechaInicio.plusMonths(12);
            default: return fechaInicio.plusMonths(6); // Valor por defecto
        }
    }


    @Override
    public NutricionistaRequerimientoDTO editarPlanAlimenticio(String dni, NutricionistaRequerimientoDTO dto) {
        Paciente paciente1 = pacienteRepositorio.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con DNI: " + dni));

        Planalimenticio plan = planAlimenticioRepositorio.findByIdpacienteId(paciente1.getId());
        if (plan == null) {
            throw new RuntimeException("No se encontró el plan alimenticio asociado al paciente");
        }

        Paciente paciente = plan.getIdpaciente();
        Plannutricional planNutricionalActual = paciente.getIdPlanNutricional();
        Integer idPlanNutricionalActual = planNutricionalActual != null ? planNutricionalActual.getId() : null;

        Integer idPlanNutricionalNuevo = dto.getIdPlanNutricional();

        // Si el plan nutricional cambia
        if (idPlanNutricionalNuevo != null && !idPlanNutricionalNuevo.equals(idPlanNutricionalActual)) {
            Plannutricional nuevoPlanNutricional = planNutricionalRepositorio.findById(idPlanNutricionalNuevo)
                    .orElseThrow(() -> new RuntimeException("Plan nutricional no encontrado"));
            paciente.setIdPlanNutricional(nuevoPlanNutricional);
            pacienteRepositorio.save(paciente);

            // Recalcular fechas
            LocalDate fechaInicio = LocalDate.now();
            LocalDate fechaFinal = calcularFechaFinal(nuevoPlanNutricional.getDuracion(), fechaInicio);
            plan.setFechainicio(fechaInicio);
            plan.setFechafin(fechaFinal);
        }

        // Validar Plan Premium
        if (paciente.getIdplan() == null
                || !"plan premium".equalsIgnoreCase(paciente.getIdplan().getTipo())) {
            throw new IllegalStateException("Solo pacientes con 'Plan Premium' pueden editar su plan alimenticio.");
        }

        boolean tieneCitas = citaRepositorio.existsByPacienteId(paciente.getId());
        if (!tieneCitas) {
            throw new IllegalStateException("El paciente no tiene ninguna cita registrada con un nutricionista.");
        }
        boolean tieneCitaAceptada = citaRepositorio.existsByPacienteIdAndEstado(paciente.getId(), "Aceptada");
        if (!tieneCitaAceptada) {
            throw new IllegalStateException("El paciente no tiene ninguna cita aceptada con un nutricionista.");
        }

        if (dto.getCaloriasDiaria() == null || dto.getCaloriasDiaria() < 0 ||
                dto.getProteinasDiaria() == null || dto.getProteinasDiaria() < 0 ||
                dto.getGrasasDiaria() == null || dto.getGrasasDiaria() < 0 ||
                dto.getCarbohidratosDiaria() == null || dto.getCarbohidratosDiaria() < 0) {
            throw new IllegalArgumentException("Los valores de nutrientes deben ser no nulos y mayores o iguales a 0");
        }

        plan.setCaloriasDiaria(dto.getCaloriasDiaria());
        plan.setProteinasDiaria(dto.getProteinasDiaria());
        plan.setGrasasDiaria(dto.getGrasasDiaria());
        plan.setCarbohidratosDiaria(dto.getCarbohidratosDiaria());

        Planalimenticio guardado = planAlimenticioRepositorio.save(plan);

        planRecetaService.recalcularPlanRecetas(guardado.getId());
        NutricionistaRequerimientoDTO respuesta = new NutricionistaRequerimientoDTO();
        respuesta.setIdPlanNutricional(dto.getIdPlanNutricional());
        respuesta.setCaloriasDiaria(dto.getCaloriasDiaria());
        respuesta.setGrasasDiaria(dto.getGrasasDiaria());
        respuesta.setCarbohidratosDiaria(dto.getCarbohidratosDiaria());
        respuesta.setProteinasDiaria(dto.getProteinasDiaria());

        return respuesta;
    }



    public PlanAlimenticioDTO recalcularPlanAlimenticioPorPaciente(Integer idPaciente) {
        Planalimenticio plan = planAlimenticioRepositorio.findByIdpacienteId(idPaciente);
        if (plan == null) {
            throw new RuntimeException("Plan alimenticio no encontrado para el paciente con ID: " + idPaciente);
        }

        Paciente paciente = pacienteRepositorio.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Plannutricional planNutricional = planNutricionalRepositorio.findById(
                        paciente.getIdPlanNutricional().getId())
                .orElseThrow(() -> new RuntimeException("Plan nutricional no encontrado"));

        double calorias = calcularCalorias(paciente, planNutricional.getObjetivo(), planNutricional.getDuracion());
        double[] macros = calcularMacronutrientes(calorias, planNutricional.getObjetivo(), paciente.getTrigliceridos().doubleValue());

        plan.setCaloriasDiaria(calorias);
        plan.setCarbohidratosDiaria(macros[0]);
        plan.setProteinasDiaria(macros[1]);
        plan.setGrasasDiaria(macros[2]);

        planAlimenticioRepositorio.save(plan);

        return modelMapper.map(plan, PlanAlimenticioDTO.class);
    }


    @Override
    public List<PlanAlimenticioDTO> findAll() {
        return planAlimenticioRepositorio.findAll()
                .stream()
                .map(planAlimenticio -> modelMapper.map(planAlimenticio, PlanAlimenticioDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanAlimenticioDTO> findAllConDatosActualizados() {
        return planAlimenticioRepositorio.findAll()
                .stream()
                .map(this::actualizarDatosCalculados)
                .collect(Collectors.toList());
    }

    @Override
    public PlanAlimenticioDTO actualizar(PlanAlimenticioDTO planAlimenticioDTO) {
        return planAlimenticioRepositorio.findById(planAlimenticioDTO.getId())
                .map(existing -> {
                    Planalimenticio planAlimenticio = modelMapper.map(planAlimenticioDTO, Planalimenticio.class);
                    Planalimenticio guardado = planAlimenticioRepositorio.save(planAlimenticio);
                    return modelMapper.map(guardado, PlanAlimenticioDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + planAlimenticioDTO.getId() + " no encontrado"));
    }

    @Override
    public PlanAlimenticioDTO consultarPlanAlimenticio(Integer idPaciente) {
        Planalimenticio planAlimenticio = planAlimenticioRepositorio.findByIdpacienteId(idPaciente);
        if (planAlimenticio == null) {
            throw new RuntimeException("Plan alimenticio no encontrado para el paciente con ID: " + idPaciente);
        }

        return modelMapper.map(planAlimenticio, PlanAlimenticioDTO.class);
    }


    /*@Override
    public PlanAlimenticioDTO consultarPlanAlimenticioConDatosActualizados(Integer idPaciente) {
        Planalimenticio planAlimenticio = planAlimenticioRepositorio.findByIdpacienteId(idPaciente);
        if (planAlimenticio == null) {
            throw new RuntimeException("Plan alimenticio no encontrado para el paciente con ID: " + idPaciente);
        }

        return actualizarDatosCalculados(planAlimenticio);
    }*/
    @Override
    public PlanAlimenticioDTO consultarPlanAlimenticioConDatosActualizados(Integer idPaciente) {
        Planalimenticio planAlimenticio = planAlimenticioRepositorio.findByIdpacienteId(idPaciente);
        if (planAlimenticio == null) {
            throw new RuntimeException("Plan alimenticio no encontrado para el paciente con ID: " + idPaciente);
        }

        return modelMapper.map(planAlimenticio, PlanAlimenticioDTO.class);
    }



    private PlanAlimenticioDTO actualizarDatosCalculados(Planalimenticio planAlimenticio) {
        try {

            Paciente paciente = pacienteRepositorio.findById(planAlimenticio.getIdpaciente().getId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));


            Plannutricional planNutricional = planNutricionalRepositorio.findById(paciente.getIdPlanNutricional().getId())
                    .orElseThrow(() -> new RuntimeException("Plan nutricional no encontrado"));

            String objetivo = planNutricional.getObjetivo();
            String duracion = planNutricional.getDuracion();


            double caloriasActualizadas = calcularCalorias(paciente, objetivo, duracion);
            double[] macrosActualizados = calcularMacronutrientes(caloriasActualizadas, objetivo, paciente.getTrigliceridos().doubleValue());


            PlanAlimenticioDTO planDTO = modelMapper.map(planAlimenticio, PlanAlimenticioDTO.class);
            planDTO.setCaloriasDiaria(caloriasActualizadas);
            planDTO.setCarbohidratosDiaria(macrosActualizados[0]);
            planDTO.setProteinasDiaria(macrosActualizados[1]);
            planDTO.setGrasasDiaria(macrosActualizados[2]);

            return planDTO;

        } catch (Exception e) {
            // Si hay algún error, devolver los datos originales del plan
            return modelMapper.map(planAlimenticio, PlanAlimenticioDTO.class);
        }
    }

    private double calcularCalorias(Paciente paciente, String objetivo, String duracion) {
        String sexo = paciente.getIdusuario().getGenero();
        double caloriesBasales = 0;

        if ("Masculino".equalsIgnoreCase(sexo)) {
            caloriesBasales = 66 + (13.75 * paciente.getPeso().doubleValue()) +
                    (5 * paciente.getAltura().doubleValue()) -
                    (6.75 * paciente.getEdad());
        } else {
            caloriesBasales = 655 + (9.56 * paciente.getPeso().doubleValue()) +
                    (1.85 * paciente.getAltura().doubleValue()) -
                    (4.68 * paciente.getEdad());
        }

        String actividadFisica = paciente.getActividadFisica();
        if ("Sedentario".equalsIgnoreCase(actividadFisica)) {
            caloriesBasales *= 1.2;
        } else if ("Moderadamente activo".equalsIgnoreCase(actividadFisica)) {
            caloriesBasales *= 1.55;
        } else if ("Muy activo".equalsIgnoreCase(actividadFisica)) {
            caloriesBasales *= 1.9;
        }

        if ("reducir nivel de trigliceridos".equalsIgnoreCase(objetivo)) {
            if (paciente.getTrigliceridos().doubleValue() > 200) {
                caloriesBasales -= 400;
            } else if (paciente.getTrigliceridos().doubleValue() > 150) {
                caloriesBasales -= 250;
            }
        } else if ("mantener mi salud".equalsIgnoreCase(objetivo)) {
            if (paciente.getTrigliceridos().doubleValue() > 150) {
                caloriesBasales -= 150;
            }
        }

        double minCalorias = "M".equalsIgnoreCase(sexo) ? 1500 : 1200;
        return Math.max(caloriesBasales, minCalorias);
    }

    private double[] calcularMacronutrientes(double calorias, String objetivo, double trigliceridos) {
        double carbohidratos, proteinas, grasas;

        if ("reducir nivel de trigliceridos".equalsIgnoreCase(objetivo)) {
            carbohidratos = calorias * 0.40 / 4;
            proteinas = calorias * 0.30 / 4;
            grasas = calorias * 0.30 / 9;
        } else if ("mantener mi salud".equalsIgnoreCase(objetivo)) {
            if (trigliceridos > 150) {
                carbohidratos = calorias * 0.45 / 4;
                proteinas = calorias * 0.25 / 4;
                grasas = calorias * 0.30 / 9;
            } else {
                carbohidratos = calorias * 0.50 / 4;
                proteinas = calorias * 0.20 / 4;
                grasas = calorias * 0.30 / 9;
            }
        } else {
            carbohidratos = calorias * 0.50 / 4;
            proteinas = calorias * 0.20 / 4;
            grasas = calorias * 0.30 / 9;
        }

        return new double[]{carbohidratos, proteinas, grasas};
    }
    public Planalimenticio obtenerPlanAlimenticioPorPaciente(String dni) {
        Paciente paciente = pacienteRepositorio.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con DNI: " + dni));

        return planAlimenticioRepositorio.findByIdpacienteId(paciente.getId());
    }


}
