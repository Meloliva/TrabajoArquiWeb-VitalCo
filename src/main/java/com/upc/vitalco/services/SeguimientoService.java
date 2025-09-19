package com.upc.vitalco.services;

import com.upc.vitalco.dto.NutricionistaxRequerimientoDTO;
import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.dto.SeguimientoDTO;
import com.upc.vitalco.entidades.Planalimenticio;
import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.entidades.Seguimiento;
import com.upc.vitalco.interfaces.ISeguimientoServices;
import com.upc.vitalco.repositorios.PlanRecetaRepositorio;
import com.upc.vitalco.repositorios.SeguimientoRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeguimientoService implements ISeguimientoServices {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SeguimientoRepositorio seguimientoRepositorio;
    @Autowired
    private PlanRecetaRepositorio planRecetaRepositorio;

    @Override
    public SeguimientoDTO registrar(SeguimientoDTO seguimientoDTO) {
        Seguimiento seguimiento = modelMapper.map(seguimientoDTO, Seguimiento.class);
        seguimiento = seguimientoRepositorio.save(seguimiento);
        return modelMapper.map(seguimiento, SeguimientoDTO.class);
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
    public String agregarRecetaADia(Integer pacienteId, LocalDate fecha, RecetaDTO recetaDTO) {
        // 1. Buscar seguimiento del día para el paciente
        List<Seguimiento> seguimientos = seguimientoRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);
        Seguimiento seguimiento;

        if (seguimientos.isEmpty()) {
            // 2. Si no existe, buscar o crear plan de receta
            Planreceta planDeReceta = planRecetaRepositorio.buscarPorPacienteYFecha(pacienteId, fecha);
            if (planDeReceta == null) {
                planDeReceta = modelMapper.map(recetaDTO, Planreceta.class);
                // asociar el plan alimenticio si hace falta
                planDeReceta = planRecetaRepositorio.save(planDeReceta);
            }

            // 3. Crear seguimiento nuevo
            seguimiento = new Seguimiento();
            seguimiento.setIdplanreceta(planDeReceta);
            seguimiento.setFecharegistro(fecha.atStartOfDay());
            seguimiento = seguimientoRepositorio.save(seguimiento);
        } else {
            // 4. Ya existe un seguimiento para ese paciente y fecha
            seguimiento = seguimientos.get(0);
        }

        // 5. Verificar cumplimiento de metas
        Planalimenticio plan = seguimiento.getIdplanreceta().getIdplanalimenticio();
        if (plan != null) {
            double caloriasSeguimiento = seguimiento.getCalorias() != null ? seguimiento.getCalorias() : 0.0;
            double caloriasMeta = plan.getCaloriasDiaria() != null ? plan.getCaloriasDiaria() : 0.0;
            boolean cumplio = caloriasSeguimiento >= caloriasMeta;

            if (cumplio) {
                seguimiento.setCumplio(true);
                seguimientoRepositorio.save(seguimiento);
                return "¡Felicitaciones! Has cumplido tu meta diaria.";
            }
        }

        seguimientoRepositorio.save(seguimiento);
        return "Receta agregada. Sigue esforzándote para cumplir tu meta.";
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
}
