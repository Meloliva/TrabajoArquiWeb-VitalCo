package com.upc.vitalco.services;
import com.upc.vitalco.dto.PacienteDTO;
import com.upc.vitalco.dto.PlanSuscripcionDTO;
import com.upc.vitalco.dto.UsuarioDTO;
import com.upc.vitalco.entidades.*;
import com.upc.vitalco.interfaces.IUsuarioServices;
import com.upc.vitalco.repositorios.PacienteRepositorio;
import com.upc.vitalco.repositorios.PlanSuscripcionRepositorio;
import com.upc.vitalco.repositorios.RolRepositorio;
import com.upc.vitalco.repositorios.UsuarioRepositorio;
import com.upc.vitalco.security.util.SecurityUtils;
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
    private PacienteRepositorio pacienteRepositorio;
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
                usuarioExistente.setFotoPerfil(usuarioDTO.getFotoPerfil());
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
    public List<UsuarioDTO> obtenerPorId(Integer pacienteId) {
        Usuario usuario = usuarioRepositorio.findUsuarioByPacienteId(pacienteId);
        if (usuario == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(modelMapper.map(usuario, UsuarioDTO.class));
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
        usuario.setCodigoVerificado(false);
        usuarioRepositorio.save(usuario);
        enviarCorreoRecuperacion(correo, codigo);
    }

    @Override
    public boolean verificarCodigo(String codigo) {
        Usuario usuario = usuarioRepositorio.findByCodigoRecuperacion(codigo);

        if (usuario == null) {
            throw new RuntimeException("Código no válido o no asociado a ningún usuario.");
        }

        if (!"Desactivado".equals(usuario.getEstado())) {
            throw new RuntimeException("La cuenta no está desactivada.");
        }

        if (usuario.getCodigoRecuperacion() == null) {
            throw new RuntimeException("No hay un código de recuperación activo.");
        }

        if (Boolean.TRUE.equals(usuario.getCodigoVerificado())) {
            throw new RuntimeException("El código ya fue utilizado. Solicita uno nuevo.");
        }

        // Como buscamos por código, ya coincide; marcamos verificado y guardamos
        usuario.setCodigoVerificado(true);
        usuarioRepositorio.save(usuario);

        return true;
    }

    private void enviarCorreoConfirmacion(String correo, String nombre) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correo);
        mensaje.setSubject("Cuenta restablecida - VitalCo");
        mensaje.setText(
                "Hola " + nombre + ",\n\n" +
                        "Tu cuenta ha sido restablecida exitosamente.\n" +
                        "Ya puedes iniciar sesión con tu nueva contraseña.\n\n" +
                        "Si no realizaste esta acción, contacta con soporte inmediatamente.\n\n" +
                        "Saludos,\n" +
                        "Equipo VitalCo"
        );
        mailSender.send(mensaje);
    }

    // java
    @Override
    public void restablecerCuenta(String correo, String nuevaContraseña) {
        Usuario usuario = usuarioRepositorio.findByCorreo(correo);
        if (usuario == null || !"Desactivado".equals(usuario.getEstado())) {
            throw new RuntimeException("No se puede restablecer la cuenta.");
        }
        usuario.setEstado("Activo");
        usuario.setContraseña(passwordEncoder.encode(nuevaContraseña));
        usuario.setCodigoRecuperacion(null);
        usuario.setCodigoVerificado(false);
        usuarioRepositorio.save(usuario);
        enviarCorreoConfirmacion(correo, usuario.getNombre());
    }

    @Override
    public UsuarioDTO obtenerPorIdIndividual(Integer id) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

}





