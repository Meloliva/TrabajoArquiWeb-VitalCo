package com.upc.vitalco.services;
import com.upc.vitalco.dto.PacienteDTO;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.interfaces.IPacienteServices;
import com.upc.vitalco.repositorios.PacienteRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService implements IPacienteServices {
    @Autowired
    private PacienteRepositorio pacienteRepositorio;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PacienteDTO registrar(PacienteDTO pacienteDTO) {
        if (pacienteDTO.getId() == null) {
            Paciente paciente = modelMapper.map(pacienteDTO, Paciente.class);
            paciente = pacienteRepositorio.save(paciente);
            return modelMapper.map(paciente, PacienteDTO.class);
        }
        return null;
    }

    @Override
    public void eliminar(Integer id) {
        if(pacienteRepositorio.existsById(id)) {
            pacienteRepositorio.deleteById(id);
        }
    }

    @Override
    public List<PacienteDTO> findAll() {
        return pacienteRepositorio.findAll()
                .stream()
                .map(paciente -> modelMapper.map(paciente, PacienteDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PacienteDTO actualizar(PacienteDTO pacienteDTO) {
        return pacienteRepositorio.findById(pacienteDTO.getId())
                .map(existing -> {
                    Paciente paciente = modelMapper.map(pacienteDTO, Paciente.class);
                    Paciente guardado = pacienteRepositorio.save(paciente);
                    return modelMapper.map(guardado, PacienteDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + pacienteDTO.getId() + " no encontrado"));
    }
}
