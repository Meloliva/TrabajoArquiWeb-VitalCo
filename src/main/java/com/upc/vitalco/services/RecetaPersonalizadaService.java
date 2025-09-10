//US02 - Implementacion de listar recetas personalizadas por paciente
/*package com.upc.vitalco.services;
import com.upc.vitalco.dto.RecetaPersonalizadaDTO;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.entidades.Plannutricional;
import com.upc.vitalco.entidades.Receta;
import com.upc.vitalco.interfaces.IRecetaPersonalizadaServices;
import com.upc.vitalco.repositorios.PacienteRepository;
import com.upc.vitalco.repositorios.PlanNutricionalRepositorio;
import com.upc.vitalco.repositorios.RecetaRepositorio;
import com.upc.vitalco.services.RecetaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecetaPersonalizadaService implements IRecetaPersonalizadaServices {
    private final PacienteRepository pacienteRepository;
    private final PlanNutricionalRepositorio plannutricionalRepository;
    private final RecetaRepositorio RecetaRepositorio;

    public RecetaServiceImpl(PacienteRepository pacienteRepository,
                             PlanNutricionalRepositorio plannutricionalRepository,
                             RecetaRepositorio recetaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.plannutricionalRepository = plannutricionalRepository;
        this.RecetaRepositorio = recetaRepository;
    }
    @Override
    public List<RecetaPersonalizadaDTO> obtenerRecetasPersonalizadasPorPaciente(Integer idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente).orElse(null);
        if (paciente == null) return List.of();

        Plannutricional plan = plannutricionalRepository.findByPacienteId(idPaciente);
        if (plan == null) return List.of();

        List<Receta> recetas = recetaRepository.findByObjetivoAndCondicionesAndPreferencias(
                plan.getObjetivo(),
                paciente.getCondiciones(),
                paciente.getPreferencias()
        );
        return recetas.stream().map(receta -> {
            RecetaPersonalizadaDTO dto = new RecetaPersonalizadaDTO();
            dto.setId(receta.getId());
            dto.setNombre(receta.getNombre());
            dto.setDescripcion(receta.getDescripcion());
            dto.setIngredientes(receta.getIngredientes());
            dto.setPreparacion(receta.getPreparacion());
            dto.setCondiciones(receta.getCondiciones());
            dto.setPreferencias(receta.getPreferencias());
            dto.setTiempo(receta.getTiempo());
            dto.setCalorias(receta.getCalorias());
            dto.setGrasas(receta.getGrasas());
            dto.setFavorito(false); // O según lógica de favoritos
            return dto;
        }).collect(Collectors.toList());
    }
}*/
