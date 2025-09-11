package com.upc.vitalco.services;

import com.upc.vitalco.dto.RolDTO;
import com.upc.vitalco.entidades.Role;
import com.upc.vitalco.interfaces.IRolServices;
import com.upc.vitalco.repositorios.RolRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService implements IRolServices {

    @Autowired
    private RolRepositorio rolRepositorio;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RolDTO registrar(RolDTO rolesDTO) {
        if (rolesDTO.getId() == null) {
            Role rol = modelMapper.map(rolesDTO, Role.class);
            rol = rolRepositorio.save(rol);
            return modelMapper.map(rol, RolDTO.class);
        }
        return null;
    }

    @Override
    public List<RolDTO> findAll() {
        return rolRepositorio.findAll()
                .stream()
                .map(rol -> modelMapper.map(rol, RolDTO.class))
                .toList();
    }

    @Override
    public void eliminarRol(Long idRol) {
        if(rolRepositorio.existsById(idRol)) {
            rolRepositorio.deleteById(idRol);
        }
    }

    @Override
    public RolDTO actualizar(RolDTO rolesDTO) {
        return rolRepositorio.findById(rolesDTO.getId())
                .map(existing -> {
                    Role roleEntidad = modelMapper.map(rolesDTO, Role.class);
                    Role guardado = rolRepositorio.save(roleEntidad);
                    return modelMapper.map(guardado, RolDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Receta con ID " + rolesDTO.getId() +
                        " no encontrado"));
    }
}
