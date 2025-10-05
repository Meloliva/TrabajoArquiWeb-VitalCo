package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.services.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecetaController {
    @Autowired
    private RecetaService recetaService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registrarReceta")
    public RecetaDTO registrar(@RequestBody RecetaDTO recetaDTO){ //wrapper
        return recetaService.registrar(recetaDTO);
    }
   /* @PostMapping("/registrarRecetaNutri")
    public RecetaDTO registrarRecetaNutri(@RequestBody RecetaDTO recetaDTO) {
        return recetaService.registrarNutri(recetaDTO);
    }*/
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listarRecetas")
    public List<RecetaDTO> findAll(){
        return recetaService.findAll();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminarReceta/{id}")
    public void eliminar(@PathVariable Long id){
        recetaService.eliminarReceta(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editarReceta")
    public ResponseEntity<RecetaDTO> editarReceta(@RequestBody RecetaDTO recetaDTO){
        return ResponseEntity.ok(recetaService.actualizar(recetaDTO));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/buscarRecetasPorNombre")
    public List<RecetaDTO> buscarPorNombre(@RequestParam String nombre) {
        return recetaService.buscarRecetasPorNombre(nombre);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/autocompletarNombresRecetas")
    public List<String> autocompletarNombres(@RequestParam String texto) {
        return recetaService.autocompletarNombresRecetas(texto);
    }


}

