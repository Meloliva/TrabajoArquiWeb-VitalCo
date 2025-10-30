package com.upc.vitalco.services;

import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.entidades.Receta;
import com.upc.vitalco.entidades.Seguimiento;
import com.upc.vitalco.interfaces.IRecetaServices;
import com.upc.vitalco.repositorios.PacienteRepositorio;
import com.upc.vitalco.repositorios.RecetaRepositorio;
import com.upc.vitalco.repositorios.SeguimientoRepositorio;
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

        recetaExistente.setNombre(recetaDTO.getNombre());
        recetaExistente.setDescripcion(recetaDTO.getDescripcion());
        recetaExistente.setTiempo(recetaDTO.getTiempo());
        recetaExistente.setCarbohidratos(recetaDTO.getCarbohidratos());
        recetaExistente.setCalorias(recetaDTO.getCalorias());
        recetaExistente.setGrasas(recetaDTO.getGrasas());
        recetaExistente.setProteinas(recetaDTO.getProteinas());

        Receta guardado = recetaRepositorio.save(recetaExistente);
        return modelMapper.map(guardado, RecetaDTO.class);
    }

}
