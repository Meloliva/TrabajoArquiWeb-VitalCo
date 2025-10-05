package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.NutricionistaDTO;
import com.upc.vitalco.services.NutricionistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NutricionistaController {
    @Autowired
    private NutricionistaService nutricionistaService;

    @PostMapping("/registrarNutricionista")
    public NutricionistaDTO registrar(@RequestBody NutricionistaDTO dto) {
        return nutricionistaService.registrar(dto);
    }

    @GetMapping("/listarNutricionistas")
    @PreAuthorize("hasRole('ADMIN')")
    public List<NutricionistaDTO> findAll(){
        return nutricionistaService.findAll();
    }

    @DeleteMapping("/eliminarNutricionista/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Integer id){
        nutricionistaService.eliminar(id);
    }

    @PutMapping("/editarNutricionista")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NutricionistaDTO> editar(@RequestBody NutricionistaDTO dto) {
        return ResponseEntity.ok(nutricionistaService.actualizar(dto));
    }
}
