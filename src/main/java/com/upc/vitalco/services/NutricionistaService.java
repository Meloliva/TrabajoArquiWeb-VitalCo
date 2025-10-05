package com.upc.vitalco.services;

import com.upc.vitalco.dto.NutricionistaDTO;
import com.upc.vitalco.entidades.Nutricionista;
import com.upc.vitalco.entidades.Turno;
import com.upc.vitalco.interfaces.INutricionistaServices;
import com.upc.vitalco.repositorios.NutricionistaRepositorio;
import com.upc.vitalco.repositorios.TurnoRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NutricionistaService implements INutricionistaServices {
    @Autowired
    private NutricionistaRepositorio nutricionistaRepositorio;
    @Autowired
    private TurnoRepositorio turnoRepositorio;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public NutricionistaDTO registrar(NutricionistaDTO nutricionistaDTO) {
        if (nutricionistaDTO.getId() == null) {
            Nutricionista nutricionista = modelMapper.map(nutricionistaDTO, Nutricionista.class);
            nutricionista = nutricionistaRepositorio.save(nutricionista);
            return modelMapper.map(nutricionista, NutricionistaDTO.class);
        }
        return null;
    }

    @Override
    public void eliminar(Integer id) {
        if(nutricionistaRepositorio.existsById(id)) {
            nutricionistaRepositorio.deleteById(id);
        }
    }

    @Override
    public List<NutricionistaDTO> findAll() {
        return nutricionistaRepositorio.findAll()
                .stream()
                .map(nutricionista -> modelMapper.map(nutricionista, NutricionistaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public NutricionistaDTO actualizar(NutricionistaDTO nutricionistaDTO) {
        Nutricionista nutricionista = nutricionistaRepositorio.findById(nutricionistaDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + nutricionistaDTO.getId() + " no encontrado"));

        // Actualizar solo los campos permitidos
        nutricionista.setAsociaciones(nutricionistaDTO.getAsociaciones());
        nutricionista.setGradoAcademico(nutricionistaDTO.getGradoAcademico());
        nutricionista.setUniversidad(nutricionistaDTO.getUniversidad());

        // Actualizar turno si viene en el DTO
        if (nutricionistaDTO.getIdturno() != null && nutricionistaDTO.getIdturno().getId() != null) {
            Turno turno = turnoRepositorio.findById(nutricionistaDTO.getIdturno().getId())
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
            nutricionista.setIdturno(turno);
        }

        Nutricionista guardado = nutricionistaRepositorio.save(nutricionista);
        return modelMapper.map(guardado, NutricionistaDTO.class);
    }

}
