// src/main/java/com/upc/vitalco/services/UsuarioService.java
package com.upc.vitalco.services;
import com.upc.vitalco.dto.UsuarioDTO;
import com.upc.vitalco.entidades.Usuario;
import com.upc.vitalco.interfaces.IUsuarioServices;
import com.upc.vitalco.repositorios.UsuarioRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements IUsuarioServices {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UsuarioDTO registrar(UsuarioDTO usuarioDTO) {
        Usuario usuarioExistente = usuarioRepositorio.findByCorreo(usuarioDTO.getCorreo());
        if (usuarioExistente != null) {
            if ("Desactivado".equalsIgnoreCase(usuarioExistente.getEstado())) {
                usuarioExistente.setEstado("Activo");
                usuarioExistente.setNombre(usuarioDTO.getNombre());
                usuarioExistente.setApellido(usuarioDTO.getApellido());
                // Actualiza otros campos necesarios
                usuarioExistente = usuarioRepositorio.save(usuarioExistente);
                return modelMapper.map(usuarioExistente, UsuarioDTO.class);
            } else {
                throw new RuntimeException("El correo ya está registrado y activo.");
            }
        }
        Usuario usuario = modelMapper.map(usuarioDTO, Usuario.class);
        usuario.setEstado("Activo");
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

    @Override
    public UsuarioDTO actualizar(UsuarioDTO usuarioDTO) {
        return usuarioRepositorio.findById(usuarioDTO.getId())
                .map(existing -> {
                    Usuario usuarioEntidad = modelMapper.map(usuarioDTO, Usuario.class);
                    Usuario guardado = usuarioRepositorio.save(usuarioEntidad);
                    return modelMapper.map(guardado, UsuarioDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + usuarioDTO.getId() + " no encontrado"));
    }
}
