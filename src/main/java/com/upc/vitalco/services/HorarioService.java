package com.upc.vitalco.services;

import com.upc.vitalco.dto.HorarioDTO;
import com.upc.vitalco.entidades.Horario;
import com.upc.vitalco.interfaces.IHorarioServices;
import com.upc.vitalco.repositorios.HorarioRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HorarioService implements IHorarioServices{
    @Autowired
    private HorarioRepositorio horarioRepositorio;
    @Autowired
    private ModelMapper modelMapper;



    @Override//solo si hay administrador sera visible en el mockup
    public HorarioDTO registrar(HorarioDTO horarioDTO) {
        if( horarioDTO.getId()==null){
            Horario horario = modelMapper.map(horarioDTO,Horario.class);
            horario= horarioRepositorio.save(horario);
            return modelMapper.map(horario,HorarioDTO.class);
        }
        return null;
    }

    @Override//solo si hay administrador sera visible en el mockup
    public void borrar(Long id) {
        if(horarioRepositorio.existsById(id)) {
            horarioRepositorio.deleteById(id);
        }
    }

    @Override
    public List<HorarioDTO> findAll() {
        return horarioRepositorio.findAll().
                stream()
                .map( horario ->modelMapper.map(horario, HorarioDTO.class ))
                .collect(Collectors.toList());
    }

    @Override
    public HorarioDTO actualizar(HorarioDTO horarioDTO) {//solo si hay administrador sera visible en la pagina
        return horarioRepositorio.findById(horarioDTO.getId())
                .map(existing -> {
                    Horario recetaEntidad = modelMapper.map(horarioDTO, Horario.class);
                    Horario guardado = horarioRepositorio.save(recetaEntidad);
                    return modelMapper.map(guardado, HorarioDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Receta con ID " +horarioDTO.getId() +
                        " no encontrado"));
    }
}
