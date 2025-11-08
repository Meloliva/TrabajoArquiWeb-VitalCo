package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.RolDTO;
import com.upc.vitalco.services.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.List;


@RestController
@RequestMapping("/api")
public class RolController {
    @Autowired
    private RolService rolService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registrarRol")
    public RolDTO registrar(@RequestBody RolDTO rolesDTO){ //wrapper
        return rolService.registrar(rolesDTO);
    }

    @GetMapping("/listarRoles")
    public List<RolDTO> findAll(){
        return rolService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminarRol/{id}")
    public void eliminar(@PathVariable Long id){
        rolService.eliminarRol(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editarRol")
    public ResponseEntity<RolDTO> editarRol(@RequestBody RolDTO rolesDTO){
        return ResponseEntity.ok(rolService.actualizar(rolesDTO));
    }

}

