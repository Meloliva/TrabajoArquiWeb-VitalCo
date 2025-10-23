package com.upc.vitalco.services;

import com.upc.vitalco.dto.EditarNutricionistaDTO;
import com.upc.vitalco.dto.NutricionistaDTO;
import com.upc.vitalco.entidades.Nutricionista;
import com.upc.vitalco.entidades.Turno;
import com.upc.vitalco.entidades.Usuario;
import com.upc.vitalco.interfaces.INutricionistaServices;
import com.upc.vitalco.repositorios.NutricionistaRepositorio;
import com.upc.vitalco.repositorios.TurnoRepositorio;
import com.upc.vitalco.repositorios.UsuarioRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private UsuarioRepositorio usuarioRepositorio; // Asegúrate de tener este repositorio

    @Override
    public NutricionistaDTO registrar(NutricionistaDTO nutricionistaDTO) {
        if (nutricionistaDTO.getIdusuario() == null) {
            throw new DataIntegrityViolationException("El usuario asociado no existe.");
        }

        // Buscar el usuario real en la base de datos
        Usuario usuario = usuarioRepositorio.findById(nutricionistaDTO.getIdusuario().getId())
                .orElseThrow(() -> new DataIntegrityViolationException("El usuario asociado no existe."));

        if (!"Activo".equals(usuario.getEstado())) {
            throw new DataIntegrityViolationException("El usuario asociado no está activo.");
        }
        // Validar que el rol sea PACIENTE (ignore case)
        String nombreRol = (usuario.getRol() != null) ? usuario.getRol().getTipo() : null;
        if (nombreRol == null || !nombreRol.equalsIgnoreCase("NUTRICIONISTA")) {
            throw new DataIntegrityViolationException("El usuario debe tener rol nutricionista.");
        }

        if (nutricionistaDTO.getId() == null) {
            Nutricionista nutricionista = modelMapper.map(nutricionistaDTO, Nutricionista.class);
            nutricionista.setIdusuario(usuario); // Asocia el usuario real
            nutricionista = nutricionistaRepositorio.save(nutricionista);
            return modelMapper.map(nutricionista, NutricionistaDTO.class);
        }
        return null;
    }

    @Override
    public List<NutricionistaDTO> findAll() {
        return nutricionistaRepositorio.findAll()
                .stream()
                .filter(nutricionista -> nutricionista.getIdusuario() != null &&
                "Activo".equals(nutricionista.getIdusuario().getEstado()))
                .map(nutricionista -> modelMapper.map(nutricionista, NutricionistaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public NutricionistaDTO actualizar(EditarNutricionistaDTO dto) {
        Nutricionista nutricionista = nutricionistaRepositorio.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + dto.getId() + " no encontrado"));

        if (nutricionista.getIdusuario() == null || !"Activo".equals(nutricionista.getIdusuario().getEstado())) {
            throw new DataIntegrityViolationException("El usuario asociado no está activo.");
        }
        if (dto.getAsociaciones() != null && !dto.getAsociaciones().isEmpty()) {
            nutricionista.setAsociaciones(dto.getAsociaciones());
        }
        if (dto.getGradoAcademico() != null && !dto.getGradoAcademico().isEmpty()) {
            nutricionista.setGradoAcademico(dto.getGradoAcademico());
        }
        if (dto.getUniversidad() != null && !dto.getUniversidad().isEmpty()) {
            nutricionista.setUniversidad(dto.getUniversidad());
        }

        if (dto.getCorreo() != null && !dto.getCorreo().isEmpty()) {
            nutricionista.getIdusuario().setCorreo(dto.getCorreo());
        }
        if (dto.getContraseña() != null && !dto.getContraseña().isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            nutricionista.getIdusuario().setContraseña(encoder.encode(dto.getContraseña()));
        }

        if (dto.getIdTurno() != null) {
            Turno turno = turnoRepositorio.findById(dto.getIdTurno())
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
            nutricionista.setIdturno(turno);
        }

        Nutricionista guardado = nutricionistaRepositorio.save(nutricionista);
        return modelMapper.map(guardado, NutricionistaDTO.class);
    }


}
