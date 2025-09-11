package com.upc.vitalco.controllers;

import com.upc.vitalco.dto.HorarioDTO;
import com.upc.vitalco.services.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HorarioController {
    @Autowired
    private HorarioService horarioService;


    @PostMapping("/registraHorario")
    public HorarioDTO registrar(@RequestBody HorarioDTO horarioDTO){
        return horarioService.registrar(horarioDTO);
    }
    @PutMapping("/actualizarHorario")
    public ResponseEntity<HorarioDTO> actualizar(@RequestBody HorarioDTO horarioDTO){
        return ResponseEntity.ok(horarioService.actualizar(horarioDTO));
    }
    @GetMapping("/listarHorarios")
    public List<HorarioDTO> findAll(){
        return  horarioService.findAll();
    }

    @DeleteMapping("/eliminarHorario/{id}")
    public void borrar(@PathVariable Long id){
        horarioService.borrar(id);
    }
}
