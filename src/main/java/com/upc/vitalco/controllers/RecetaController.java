package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.services.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecetaController {
    @Autowired
    private RecetaService recetaService;

    @PostMapping("/registrarReceta")
    public RecetaDTO registrar(@RequestBody RecetaDTO recetaDTO){ //wrapper
        return recetaService.registrar(recetaDTO);
    }
    @GetMapping("/listarRecetas")
    public List<RecetaDTO> findAll(){
        return recetaService.findAll();
    }

    @DeleteMapping("/eliminarReceta/{id}")
    public void eliminar(@PathVariable Long id){
        recetaService.eliminarReceta(id);
    }

    @PutMapping("/editarReceta")
    public ResponseEntity<RecetaDTO> editarReceta(@RequestBody RecetaDTO recetaDTO){
        return ResponseEntity.ok(recetaService.actualizar(recetaDTO));
    }
    @GetMapping("/buscarRecetasPorNombre")
    public List<RecetaDTO> buscarPorNombre(@RequestParam String nombre) {
        return recetaService.buscarPorNombre(nombre);
    }

    @GetMapping("/autocompletarNombresRecetas")
    public List<String> autocompletarNombres(@RequestParam String texto) {
        return recetaService.autocompletarNombresRecetas(texto);
    }


}

