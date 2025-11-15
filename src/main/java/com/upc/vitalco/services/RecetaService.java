package com.upc.vitalco.services;

import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.entidades.Receta;
import com.upc.vitalco.entidades.Horario;
import com.upc.vitalco.interfaces.IRecetaServices;
import com.upc.vitalco.repositorios.HorarioRepositorio;
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
    private ModelMapper modelMapper;

    @Override
    public RecetaDTO registrar(RecetaDTO recetaDTO) {
        if (recetaDTO.getIdReceta() == null) {
            Receta receta = modelMapper.map(recetaDTO, Receta.class);
            receta = recetaRepositorio.save(receta);//insert into
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
        Receta recetaExistente = recetaRepositorio.findById(recetaDTO.getIdReceta())
                .orElseThrow(() -> new RuntimeException("Receta con ID " + recetaDTO.getIdReceta() + " no encontrado"));

        // Actualizar horario
        if (recetaDTO.getIdhorario() != null) {
            Horario horario = horarioRepositorio.findById(recetaDTO.getIdhorario().getId())
                    .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
            recetaExistente.setIdhorario(horario);
        }

        recetaExistente.setNombre(recetaDTO.getNombre());
        recetaExistente.setDescripcion(recetaDTO.getDescripcion());
        recetaExistente.setTiempo(recetaDTO.getTiempo());
        recetaExistente.setCarbohidratos(recetaDTO.getCarbohidratos());
        recetaExistente.setCalorias(recetaDTO.getCalorias());
        recetaExistente.setGrasas(recetaDTO.getGrasas());
        recetaExistente.setProteinas(recetaDTO.getProteinas());

        // ❗ Estos estaban faltando
        recetaExistente.setIngredientes(recetaDTO.getIngredientes());
        recetaExistente.setPreparacion(recetaDTO.getPreparacion());
        recetaExistente.setCantidadPorcion(recetaDTO.getCantidadPorcion());
        if (recetaDTO.getFoto() != null && !recetaDTO.getFoto().isEmpty()) {
            recetaExistente.setFoto(recetaDTO.getFoto());
        }

        Receta guardado = recetaRepositorio.save(recetaExistente);
        return modelMapper.map(guardado, RecetaDTO.class);
    }


}
