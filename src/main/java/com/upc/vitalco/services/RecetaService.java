package com.upc.vitalco.services;

import com.upc.vitalco.dto.RecetaDTO;
import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.entidades.Receta;
import com.upc.vitalco.interfaces.IRecetaServices;
import com.upc.vitalco.repositorios.CitaRepositorio;
import com.upc.vitalco.repositorios.PlanRecetaRepositorio;
import com.upc.vitalco.repositorios.RecetaRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecetaService implements IRecetaServices {
    @Autowired
    private RecetaRepositorio recetaRepositorio;
    @Autowired
    private CitaRepositorio citaRepositorio;


    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RecetaDTO registrar(RecetaDTO recetaDTO) {
        if (recetaDTO.getIdReceta() == null) {
            Receta receta = modelMapper.map(recetaDTO, Receta.class);
            receta = recetaRepositorio.save(receta);//insert into
            return modelMapper.map(receta, RecetaDTO.class);
        }
        return null;
    }
    /*@Override
    public RecetaDTO agregarRecetaAPaciente(RecetaDTO recetaDTO, Integer pacienteId, Integer nutricionistaId) {
    boolean tieneCitaAceptada = citaRepositorio.existsByPacienteIdAndNutricionistaIdAndEstado(
            pacienteId, nutricionistaId, "Aceptada");
    if (!tieneCitaAceptada) {
        throw new IllegalStateException("No existe una cita aceptada entre el paciente y el nutricionista.");
    }
    Receta receta = modelMapper.map(recetaDTO, Receta.class);
    // Aquí puedes asociar paciente y nutricionista a la receta si tu modelo lo permite
    receta = recetaRepositorio.save(receta);
    return modelMapper.map(receta, RecetaDTO.class);
}*/

    @Override
    public List<RecetaDTO> findAll() {
        return recetaRepositorio.findAll().
                stream()
                .map(receta -> modelMapper.map(receta, RecetaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void eliminarReceta(Long idReceta) {//solo si hay administrador sera visible en la pagina
        if (recetaRepositorio.existsById(idReceta)) {
            recetaRepositorio.deleteById(idReceta);
        }
    }

    @Override
    public RecetaDTO actualizar(RecetaDTO recetaDTO) {
        return recetaRepositorio.findById(recetaDTO.getIdReceta())
                .map(existing -> {
                    Receta recetaEntidad = modelMapper.map(recetaDTO, Receta.class);
                    Receta guardado = recetaRepositorio.save(recetaEntidad);
                    return modelMapper.map(guardado, RecetaDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Receta con ID " + recetaDTO.getIdReceta() +
                        " no encontrado"));
    }

    @Override
    public List<RecetaDTO> buscarRecetasPorNombre(String nombre) {
        return recetaRepositorio.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(receta -> modelMapper.map(receta, RecetaDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> autocompletarNombresRecetas(String texto) {
        return recetaRepositorio.findByNombreContainingIgnoreCase(texto)
                .stream()
                .map(Receta::getNombre)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }
    //falta filtro de listar por horario
}
