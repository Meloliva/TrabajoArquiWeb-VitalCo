package com.upc.vitalco.controllers;
import com.upc.vitalco.dto.UsuarioDTO;
import com.upc.vitalco.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrarUsuario")
    public ResponseEntity<UsuarioDTO> registrar(@RequestBody UsuarioDTO dto) {
        UsuarioDTO registrado = usuarioService.registrar(dto);
        return ResponseEntity.ok(registrado);
    }

    @GetMapping("/listarUsuarios")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> lista = usuarioService.findAll();
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/eliminarUsuario/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/editarUsuario/{id}")
    public ResponseEntity<UsuarioDTO> editar(@RequestBody UsuarioDTO dto) {
        UsuarioDTO actualizado = usuarioService.actualizar(dto);
        return ResponseEntity.ok(actualizado);
    }
}
