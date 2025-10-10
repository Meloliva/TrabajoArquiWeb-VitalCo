package com.upc.vitalco.services;
import com.upc.vitalco.dto.PacienteDTO;
import com.upc.vitalco.dto.UsuarioDTO;
import com.upc.vitalco.entidades.*;
import com.upc.vitalco.interfaces.IUsuarioServices;
import com.upc.vitalco.repositorios.PacienteRepositorio;
import com.upc.vitalco.repositorios.PlanSuscripcionRepositorio;
import com.upc.vitalco.repositorios.RolRepositorio;
import com.upc.vitalco.repositorios.UsuarioRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements IUsuarioServices {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RolRepositorio rolRepositorio;
    @Autowired
    private JavaMailSender mailSender;


    @Override
    public UsuarioDTO registrar(UsuarioDTO usuarioDTO) {
        // Validación: JSON malformado o campos obligatorios vacíos
        if (usuarioDTO == null ||
                usuarioDTO.getDni() == null || usuarioDTO.getDni().isBlank() ||
                usuarioDTO.getCorreo() == null || usuarioDTO.getCorreo().isBlank() ||
                usuarioDTO.getNombre() == null || usuarioDTO.getNombre().isBlank() ||
                usuarioDTO.getApellido() == null || usuarioDTO.getApellido().isBlank()) {
            throw new HttpMessageNotReadableException("Datos de Usuario incompletos o malformados");
        }

        // Validación duplicado de DNI
        if (usuarioRepositorio.findAll().stream()
                .anyMatch(u -> u.getDni().equalsIgnoreCase(usuarioDTO.getDni()))) {
            throw new DataIntegrityViolationException(
                    "El DNI " + usuarioDTO.getDni() + " ya existe en la base de datos."
            );
        }

        // Validación duplicado de correo
        Usuario usuarioExistente = usuarioRepositorio.findByCorreo(usuarioDTO.getCorreo());
        if (usuarioExistente != null) {
            if ("Desactivado".equalsIgnoreCase(usuarioExistente.getEstado())) {
                usuarioExistente.setEstado("Activo");
                usuarioExistente.setNombre(usuarioDTO.getNombre());
                usuarioExistente.setApellido(usuarioDTO.getApellido());
                usuarioExistente = usuarioRepositorio.save(usuarioExistente);
                return modelMapper.map(usuarioExistente, UsuarioDTO.class);
            } else {
                throw new RuntimeException("El correo ya está registrado y activo.");
            }
        }

        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
        usuario.setEstado("Activo");

        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        Role rol = rolRepositorio.findById(usuarioDTO.getRol().getId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rol);
        usuario = usuarioRepositorio.save(usuario);
        return modelMapper.map(usuario, UsuarioDTO.class);
    }



    @Override
    public void eliminar(Integer id) {
        usuarioRepositorio.findById(id).ifPresent(usuario -> {
            usuario.setEstado("Desactivado");
            usuarioRepositorio.save(usuario);
        });
    }

    @Override //si es que hay administrador se hara la lista
    public List<UsuarioDTO> findAll() {
        return usuarioRepositorio.findAll()
                .stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioDTO.class))
                .collect(Collectors.toList());
    }

    // Genera un código aleatorio de 6 dígitos
    private String generarCodigoVerificacion() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    private void enviarCorreoRecuperacion(String correo, String codigo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correo);
        mensaje.setSubject("Recuperación de cuenta - VitalCo");
        mensaje.setText("Tu código de recuperación es: " + codigo);
        mailSender.send(mensaje);
    }

    @Override
    public void solicitarRecuperacion(String correo) {
        Usuario usuario = usuarioRepositorio.findByCorreo(correo);
        if (usuario == null || !"Desactivado".equals(usuario.getEstado())) {
            throw new RuntimeException("Correo no válido o usuario activo.");
        }
        String codigo = generarCodigoVerificacion();
        usuario.setCodigoRecuperacion(codigo);
        usuarioRepositorio.save(usuario);
        enviarCorreoRecuperacion(correo, codigo);
    }

    @Override
    public boolean verificarCodigo(String correo, String codigo) {
        Usuario usuario = usuarioRepositorio.findByCorreo(correo);
        return usuario != null && codigo.equals(usuario.getCodigoRecuperacion());
    }

    @Override
    public void restablecerCuenta(String correo, String nuevaContraseña, String codigo) {
        Usuario usuario = usuarioRepositorio.findByCorreo(correo);
        if (usuario == null || !"Desactivado".equals(usuario.getEstado())) {
            throw new RuntimeException("No se puede restablecer la cuenta.");
        }
        if (!codigo.equals(usuario.getCodigoRecuperacion())) {
            throw new RuntimeException("Código de recuperación inválido.");
        }
        usuario.setEstado("Activo");
        usuario.setContraseña(passwordEncoder.encode(nuevaContraseña));
        usuario.setCodigoRecuperacion(null);
        usuarioRepositorio.save(usuario);
    }



}
