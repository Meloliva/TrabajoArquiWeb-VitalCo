package com.upc.vitalco.services;
import com.upc.vitalco.dto.*;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.entidades.Usuario;
import com.upc.vitalco.interfaces.IPacienteServices;
import com.upc.vitalco.repositorios.PacienteRepositorio;
import com.upc.vitalco.repositorios.PlanSuscripcionRepositorio;
import com.upc.vitalco.repositorios.UsuarioRepositorio;
import com.upc.vitalco.security.util.SecurityUtils;
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

    @Autowired
    private SecurityUtils securityUtils;

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
            Usuario usuario = paciente.getIdusuario();

            if (nuevoCorreo != null && !nuevoCorreo.isBlank()) {
                usuario.setCorreo(nuevoCorreo.trim());
            }

            String nuevaContrasena = editarPacienteDTO.getContraseña();
            if (nuevaContrasena != null && !nuevaContrasena.isBlank()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                usuario.setContraseña(encoder.encode(nuevaContrasena));
            }
            String nuevaFotoUrl = editarPacienteDTO.getFotoPerfil();
            if (nuevaFotoUrl != null && !nuevaFotoUrl.isBlank()) {
                usuario.setFotoPerfil(nuevaFotoUrl.trim());
            }

            usuarioRepositorio.save(usuario);
        }

        // Actualizar datos de paciente
        paciente.setPeso(editarPacienteDTO.getPeso() != null ? editarPacienteDTO.getPeso() : paciente.getPeso());
        // ✅ Guardar edad manualmente ingresada
        Integer edadManual = editarPacienteDTO.getEdad();
        paciente.setEdad(edadManual != null ? edadManual : paciente.getEdad());
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

        // ✅ PRIMERO guardar el paciente con la edad manual
        Paciente guardado = pacienteRepositorio.save(paciente);

        // ✅ LUEGO recalcular el plan (sin tocar la edad)
        planAlimenticioService.recalcularPlanAlimenticioPorPaciente(guardado.getId());

        // ✅ VOLVER A ESTABLECER la edad manual después del recálculo
        guardado.setEdad(edadManual != null ? edadManual : guardado.getEdad());
        Paciente finalizado = pacienteRepositorio.save(guardado);

        return modelMapper.map(finalizado, PacienteDTO.class);
    }


    @Override
    public PacienteDTO obtenerPorUsuario(Integer idUsuario) {
        // Busca el paciente asociado al usuario
        Paciente paciente = pacienteRepositorio.findByIdusuario_Id(idUsuario)
                .orElseThrow(() -> new RuntimeException("No se encontró el paciente para este usuario"));

        // Convierte la entidad a DTO
        PacienteDTO dto = modelMapper.map(paciente, PacienteDTO.class);


        return dto;
    }

    public Integer obtenerIdPacientePorUsuario(Integer idusuario) {
        Paciente paciente = pacienteRepositorio.findByIdusuarioId(idusuario)
                .orElseThrow(() -> new RuntimeException("No existe un paciente asociado al usuario con ID " + idusuario));
        return paciente.getId();
    }


}




