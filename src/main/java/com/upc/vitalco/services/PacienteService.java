package com.upc.vitalco.services;
import com.upc.vitalco.dto.PacienteDTO;
import com.upc.vitalco.dto.PlanAlimenticioDTO;
import com.upc.vitalco.dto.PlanSuscripcionDTO;
import com.upc.vitalco.dto.UsuarioDTO;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.entidades.Planalimenticio;
import com.upc.vitalco.interfaces.IPacienteServices;
import com.upc.vitalco.repositorios.PacienteRepositorio;
import com.upc.vitalco.repositorios.PlanAlimenticioRepositorio;
import com.upc.vitalco.repositorios.UsuarioRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacienteService implements IPacienteServices {
    @Autowired
    private PacienteRepositorio pacienteRepositorio;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PlanAlimenticioService planAlimenticioService;
    @Autowired
    private PlanAlimenticioRepositorio planAlimenticioRepositorio;
    @Autowired
    private PlanRecetaService planRecetaService;
    @Autowired
    private SeguimientoService seguimientoService;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public PacienteDTO registrar(PacienteDTO pacienteDTO) {
        // Validación: JSON malformado o campos obligatorios vacíos
        if (pacienteDTO == null || pacienteDTO.getIdusuario() == null || pacienteDTO.getIdusuario().getId() == null) {
            throw new HttpMessageNotReadableException("Debe indicar el id del usuario");
        }

        // Validación de DNI duplicado
        String dni = pacienteDTO.getIdusuario().getDni();
        if (usuarioRepositorio.findAll().stream()
                .anyMatch(u -> u.getDni().equalsIgnoreCase(dni))) {
            throw new DataIntegrityViolationException(
                    "El DNI " + dni + " ya existe en la base de datos."
            );
        }

        if (pacienteDTO.getId() == null) {
            Paciente paciente = modelMapper.map(pacienteDTO, Paciente.class);
            paciente = pacienteRepositorio.save(paciente);
            planAlimenticioService.registrar(paciente.getId());
            return modelMapper.map(paciente, PacienteDTO.class);
        }
        return null;
    }


    @Override
    public void eliminar(Integer id) {
        if (pacienteRepositorio.existsById(id)) {
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
        Paciente paciente = pacienteRepositorio.findById(pacienteDTO.getId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (paciente.getIdusuario() != null && pacienteDTO.getIdusuario() != null) {
            String genero = pacienteDTO.getIdusuario().getGenero();
            if (genero != null) {
                paciente.getIdusuario().setGenero(genero);
                usuarioRepositorio.save(paciente.getIdusuario());
            }
        }

        paciente.setPeso(pacienteDTO.getPeso());
        paciente.setEdad(pacienteDTO.getEdad());
        paciente.setAltura(pacienteDTO.getAltura());
        paciente.setTrigliceridos(pacienteDTO.getTrigliceridos());

        if (pacienteDTO.getIdplan() != null) {
            paciente.setIdplan(modelMapper.map(pacienteDTO.getIdplan(), com.upc.vitalco.entidades.Plansuscripcion.class));
        }

        Paciente guardado = pacienteRepositorio.save(paciente);

        planAlimenticioService.recalcularPlanAlimenticioPorPaciente(guardado.getId());

        return modelMapper.map(guardado, PacienteDTO.class);
    }
}




