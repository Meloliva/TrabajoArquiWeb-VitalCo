package com.upc.vitalco.services;
import com.upc.vitalco.dto.*;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.entidades.Usuario;
import com.upc.vitalco.interfaces.IPacienteServices;
import com.upc.vitalco.repositorios.PacienteRepositorio;
import com.upc.vitalco.repositorios.PlanSuscripcionRepositorio;
import com.upc.vitalco.repositorios.UsuarioRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private PlanSuscripcionRepositorio planSuscripcionRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public PacienteDTO registrar(PacienteDTO pacienteDTO) {
        // Validación: JSON malformado o campos obligatorios vacíos
        if (pacienteDTO == null || pacienteDTO.getIdusuario() == null || pacienteDTO.getIdusuario().getId() == null) {
            throw new HttpMessageNotReadableException("Debe indicar el id del usuario");
        }
        Usuario usuario = usuarioRepositorio.findById(pacienteDTO.getIdusuario().getId())
                .orElseThrow(() -> new DataIntegrityViolationException("El usuario asociado no existe."));

        if (!"Activo".equals(usuario.getEstado())) {
            throw new DataIntegrityViolationException("El usuario asociado no está activo.");
        }

        // Validar que el rol sea PACIENTE (ignore case)
        String nombreRol = (usuario.getRol() != null) ? usuario.getRol().getTipo() : null;
        if (nombreRol == null || !nombreRol.equalsIgnoreCase("PACIENTE")) {
            throw new DataIntegrityViolationException("El usuario debe tener rol PACIENTE.");
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
        Paciente paciente = pacienteRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        if (paciente.getIdusuario() == null || !"Activo".equals(paciente.getIdusuario().getEstado())) {
            throw new DataIntegrityViolationException("El usuario asociado no está activo.");
        }

        if (pacienteRepositorio.existsById(id)) {
            pacienteRepositorio.deleteById(id);
        }
    }

    @Override
    public List<PacienteDTO> findAll() {
        return pacienteRepositorio.findAll()
                .stream()
                .filter(paciente -> paciente.getIdusuario() != null &&
                        "Activo".equals(paciente.getIdusuario().getEstado()))
                .map(paciente -> modelMapper.map(paciente, PacienteDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public PacienteDTO actualizar(EditarPacienteDTO editarPacienteDTO) {
        Paciente paciente = pacienteRepositorio.findById(editarPacienteDTO.getId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        if (paciente.getIdusuario() == null || !"Activo".equals(paciente.getIdusuario().getEstado())) {
            throw new DataIntegrityViolationException("El usuario asociado no está activo.");
        }

        // Validar correo único
        String nuevoCorreo = editarPacienteDTO.getCorreo();
        if (nuevoCorreo != null && usuarioRepositorio.findAll().stream()
                .anyMatch(u -> u.getCorreo().equalsIgnoreCase(nuevoCorreo) && !u.getId().equals(paciente.getIdusuario().getId()))) {
            throw new DataIntegrityViolationException("El correo " + nuevoCorreo + " ya existe en la base de datos.");
        }

        // Actualizar datos de usuario
        if (paciente.getIdusuario() != null) {
            paciente.getIdusuario().setCorreo(nuevoCorreo != null ? nuevoCorreo : paciente.getIdusuario().getCorreo());
            if (editarPacienteDTO.getContraseña() != null) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                paciente.getIdusuario().setContraseña(encoder.encode(editarPacienteDTO.getContraseña()));
            }
            usuarioRepositorio.save(paciente.getIdusuario());
        }

        // Actualizar datos de paciente
        paciente.setPeso(editarPacienteDTO.getPeso() != null ? editarPacienteDTO.getPeso() : paciente.getPeso());
        paciente.setEdad(editarPacienteDTO.getEdad() != null ? editarPacienteDTO.getEdad() : paciente.getEdad());
        paciente.setAltura(editarPacienteDTO.getAltura() != null ? editarPacienteDTO.getAltura() : paciente.getAltura());
        paciente.setTrigliceridos(editarPacienteDTO.getTrigliceridos() != null ? editarPacienteDTO.getTrigliceridos() : paciente.getTrigliceridos());

        // Actualizar plan suscripción solo si viene en el DTO
        if (editarPacienteDTO.getPlanSuscripcion() != null) {
            paciente.setIdplan(planSuscripcionRepositorio.findByTipo((editarPacienteDTO.getPlanSuscripcion()))
                    .orElseThrow(() -> new DataIntegrityViolationException("El plan de suscripción no es válido.")));
        }

        // Validar que el paciente tenga un plan antes de guardar
        if (paciente.getIdplan() == null) {
            throw new DataIntegrityViolationException("El paciente debe tener un plan de suscripción.");
        }

        Paciente guardado = pacienteRepositorio.save(paciente);
        planAlimenticioService.recalcularPlanAlimenticioPorPaciente(guardado.getId());

        return modelMapper.map(guardado, PacienteDTO.class);
    }

}




