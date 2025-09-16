package com.upc.vitalco.services;

import com.upc.vitalco.dto.TurnoDTO;
import com.upc.vitalco.entidades.Turno;
import com.upc.vitalco.interfaces.ITurnoServices;
import com.upc.vitalco.repositorios.TurnoRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnoService implements ITurnoServices {
    @Autowired
    private TurnoRepositorio turnoRepositorio;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TurnoDTO registrar(TurnoDTO turnoDTO) {//solo si hay administrador sera visible en la pagina
        if( turnoDTO.getId()==null){
            Turno turno = modelMapper.map(turnoDTO,Turno.class);
            turno= turnoRepositorio.save(turno);
            return modelMapper.map(turno,TurnoDTO.class);
        }
        return null;
    }

    @Override
    public void eliminar(Integer id) {
        if(turnoRepositorio.existsById(id)) {
            turnoRepositorio.deleteById(id);
        }
    }

    @Override
    public List<TurnoDTO> findAll() {
        return turnoRepositorio.findAll().
                stream()
                .map( turno ->modelMapper.map(turno, TurnoDTO.class ))
                .collect(Collectors.toList());
    }

    @Override
    public TurnoDTO actualizar(TurnoDTO turnoDTO) {
        return turnoRepositorio.findById(turnoDTO.getId())
                .map(existing -> {
                    Turno turnoEntidad = modelMapper.map(turnoDTO, Turno.class);
                    Turno guardado = turnoRepositorio.save(turnoEntidad);
                    return modelMapper.map(guardado, TurnoDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Turno con ID "
                        + turnoDTO.getId() + " no encontrado"));
    }
}
