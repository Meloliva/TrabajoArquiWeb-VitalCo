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
        List<Seguimiento> seguimientos = seguimientoRepositorio.findByPacienteIdAndFecha(pacienteId, fecha);
        return seguimientos.stream()
                .map(s -> modelMapper.map(s, SeguimientoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String agregarRecetaADia(Integer pacienteId, LocalDate fecha, RecetaDTO recetaDTO) {
        // Buscar el plan de receta correspondiente
        Planreceta planDeReceta = planRecetaRepositorio.findByIdplanalimenticio_Idpaciente_IdAndFecha(pacienteId, fecha);
        if (planDeReceta == null) {
            planDeReceta = modelMapper.map(recetaDTO, Planreceta.class);
            // asociar el plan alimenticio correspondiente si es necesario
            planDeReceta = planRecetaRepositorio.save(planDeReceta);
        }

        // Buscar seguimiento del día, si no existe, crear uno nuevo
        Planreceta finalPlanDeReceta = planDeReceta;
        Optional<Seguimiento> seguimientoOpt = seguimientoRepositorio.findByIdplanreceta(planDeReceta);
        Seguimiento seguimiento = seguimientoOpt.orElseGet(() -> {
            Seguimiento nuevo = new Seguimiento();
            nuevo.setIdplanreceta(finalPlanDeReceta);
            nuevo.setFecharegistro(fecha.atStartOfDay());
            return seguimientoRepositorio.save(nuevo);
        });
        // Verificar cumplimiento de metas
        Planalimenticio plan = planDeReceta.getIdplanalimenticio();
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
