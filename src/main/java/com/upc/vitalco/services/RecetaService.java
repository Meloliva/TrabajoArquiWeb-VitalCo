package com.upc.vitalco.services;

import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.entidades.PlanRecetaReceta;
import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.entidades.Receta;
import com.upc.vitalco.entidades.Horario;
import com.upc.vitalco.interfaces.IRecetaServices;
import com.upc.vitalco.repositorios.HorarioRepositorio;
import com.upc.vitalco.repositorios.PlanRecetaRecetaRepositorio;
import com.upc.vitalco.repositorios.PlanRecetaRepositorio;
import com.upc.vitalco.repositorios.RecetaRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecetaService implements IRecetaServices {
    @Autowired
    private RecetaRepositorio recetaRepositorio;
    @Autowired
    private HorarioRepositorio horarioRepositorio;
    @Autowired
    private PlanRecetaRepositorio planRecetaRepositorio;
    @Autowired
    private PlanRecetaRecetaRepositorio planRecetaRecetaRepositorio;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RecetaDTO registrar(RecetaDTO recetaDTO) {
        if (recetaDTO.getIdReceta() == null) {
            Receta receta = modelMapper.map(recetaDTO, Receta.class);

            // Asignar el horario si viene en el DTO
            if (recetaDTO.getIdhorario() != null) {
                Horario horario = horarioRepositorio.findById(recetaDTO.getIdhorario().getId())
                        .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
                receta.setIdhorario(horario);
            }

            // Guardar la receta globalmente
            receta = recetaRepositorio.save(receta);

            // 2. LÓGICA AGREGADA: PROPAGAR A PLANES EXISTENTES
            // Obtenemos todos los planes de receta activos
            List<Planreceta> planesExistentes = planRecetaRepositorio.findAll();

            for (Planreceta plan : planesExistentes) {
                // Obtenemos el límite calórico del plan alimenticio de ese paciente
                Double caloriasLimite = plan.getIdplanalimenticio().getCaloriasDiaria();
                Double caloriasReceta = (receta.getCalorias() != null) ? receta.getCalorias() : 0.0;

                // Verificamos si la nueva receta encaja en el plan (misma lógica que usas al crear el plan)
                if (caloriasReceta <= caloriasLimite) {
                    PlanRecetaReceta nuevaRelacion = new PlanRecetaReceta();
                    nuevaRelacion.setPlanreceta(plan);
                    nuevaRelacion.setReceta(receta);
                    nuevaRelacion.setFecharegistro(LocalDate.now());
                    nuevaRelacion.setFavorito(false);

                    // Guardamos el vínculo
                    planRecetaRecetaRepositorio.save(nuevaRelacion);
                }
            }

            return modelMapper.map(receta, RecetaDTO.class);
        }
        return null;
    }

    @Override
    public List<RecetaDTO> findAll() {
        return recetaRepositorio.findAll().
                stream()
                .map(receta -> modelMapper.map(receta, RecetaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarReceta(Long idReceta) {//solo si hay administrador sera visible en la pagina
        if (recetaRepositorio.existsById(idReceta)) {
            recetaRepositorio.deleteById(idReceta);
        }
    }

    @Override
    public RecetaDTO actualizar(RecetaDTO recetaDTO) {
        // 1. Buscar la receta existente
        Receta recetaExistente = recetaRepositorio.findById(recetaDTO.getIdReceta())
                .orElseThrow(() -> new RuntimeException("Receta con ID " + recetaDTO.getIdReceta() + " no encontrado"));

        // 2. Actualizar Horario si cambió
        if (recetaDTO.getIdhorario() != null) {
            Horario horario = horarioRepositorio.findById(recetaDTO.getIdhorario().getId())
                    .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
            recetaExistente.setIdhorario(horario);
        }

        // 3. Actualizar todos los campos básicos
        recetaExistente.setNombre(recetaDTO.getNombre());
        recetaExistente.setDescripcion(recetaDTO.getDescripcion());
        recetaExistente.setTiempo(recetaDTO.getTiempo());
        recetaExistente.setCarbohidratos(recetaDTO.getCarbohidratos());
        recetaExistente.setCalorias(recetaDTO.getCalorias());
        recetaExistente.setGrasas(recetaDTO.getGrasas());
        recetaExistente.setProteinas(recetaDTO.getProteinas());
        recetaExistente.setIngredientes(recetaDTO.getIngredientes());
        recetaExistente.setPreparacion(recetaDTO.getPreparacion());
        recetaExistente.setCantidadPorcion(recetaDTO.getCantidadPorcion());

        if (recetaDTO.getFoto() != null && !recetaDTO.getFoto().isEmpty()) {
            recetaExistente.setFoto(recetaDTO.getFoto());
        }

        // 4. Guardar los cambios
        Receta guardado = recetaRepositorio.save(recetaExistente);

        // 5. LÓGICA DE RE-ASIGNACIÓN (Igual que en registrar, pero verificando duplicados)
        List<Planreceta> planesExistentes = planRecetaRepositorio.findAll();

        for (Planreceta plan : planesExistentes) {
            Double caloriasLimite = plan.getIdplanalimenticio().getCaloriasDiaria();
            Double caloriasReceta = (guardado.getCalorias() != null) ? guardado.getCalorias() : 0.0;

            // Verificar si YA existe la relación en este plan para no duplicar
            List<PlanRecetaReceta> relacionesDelPlan = planRecetaRecetaRepositorio.findByPlanreceta(plan);
            boolean yaExiste = relacionesDelPlan.stream()
                    .anyMatch(rel -> rel.getReceta().getId().equals(guardado.getId()));

            // Caso A: La receta AHORA cumple con el plan y NO estaba asignada -> AGREGAR
            if (caloriasReceta <= caloriasLimite && !yaExiste) {
                PlanRecetaReceta nuevaRelacion = new PlanRecetaReceta();
                nuevaRelacion.setPlanreceta(plan);
                nuevaRelacion.setReceta(guardado);
                nuevaRelacion.setFecharegistro(LocalDate.now());
                nuevaRelacion.setFavorito(false);
                planRecetaRecetaRepositorio.save(nuevaRelacion);
            }

            // Caso B (Opcional): Si ya no cumple (subieron las calorías), podrías eliminarla.
            // NOTA: Eliminar es delicado si el paciente ya tiene un historial (Seguimiento) con esa receta.
            // Por seguridad, muchas apps prefieren dejarla si ya estaba asignada, o usar borrado lógico.
            // Si quieres ser estricto, aquí agregarías el 'else if' para borrar.
        }

        return modelMapper.map(guardado, RecetaDTO.class);
    }


}
