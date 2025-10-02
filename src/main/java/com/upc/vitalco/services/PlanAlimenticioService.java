package com.upc.vitalco.services;

import com.upc.vitalco.dto.PlanAlimenticioDTO;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.entidades.Planalimenticio;
import com.upc.vitalco.entidades.Plannutricional;
import com.upc.vitalco.interfaces.IPlanAlimenticioServices;
import com.upc.vitalco.repositorios.PacienteRepositorio;
import com.upc.vitalco.repositorios.PlanAlimenticioRepositorio;

import com.upc.vitalco.repositorios.PlanNutricionalRepositorio;
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


    @Override
    public PlanAlimenticioDTO consultarPlanAlimenticioConDatosActualizados(Integer idPaciente) {
        Planalimenticio planAlimenticio = planAlimenticioRepositorio.findByIdpacienteId(idPaciente);
        if (planAlimenticio == null) {
            throw new RuntimeException("Plan alimenticio no encontrado para el paciente con ID: " + idPaciente);
        }

        return actualizarDatosCalculados(planAlimenticio);
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

        if ("M".equalsIgnoreCase(sexo)) {
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

}
