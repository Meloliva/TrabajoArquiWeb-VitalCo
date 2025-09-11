package com.upc.vitalco.services;

import com.upc.vitalco.dto.NutricionistaDTO;
import com.upc.vitalco.entidades.Nutricionista;
import com.upc.vitalco.interfaces.INutricionistaServices;
import com.upc.vitalco.repositorios.NutricionistaRepositorio;
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
        return nutricionistaRepositorio.findById(nutricionistaDTO.getId())
                .map(existing -> {
                    Nutricionista nutricionista = modelMapper.map(nutricionistaDTO, Nutricionista.class);
                    Nutricionista guardado = nutricionistaRepositorio.save(nutricionista);
                    return modelMapper.map(guardado, NutricionistaDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + nutricionistaDTO.getId() + " no encontrado"));
    }
}
