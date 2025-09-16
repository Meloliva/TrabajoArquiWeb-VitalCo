package com.upc.vitalco.services;

import com.upc.vitalco.dto.CitaDTO;
import com.upc.vitalco.entidades.Cita;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.entidades.Nutricionista;
import com.upc.vitalco.interfaces.ICitaServices;
import com.upc.vitalco.repositorios.CitaRepositorio;
import com.upc.vitalco.repositorios.PacienteRepositorio;
import com.upc.vitalco.repositorios.NutricionistaRepositorio;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaService implements ICitaServices {
    @Autowired
    private CitaRepositorio citaRepositorio;

    @Autowired
    private PacienteRepositorio pacienteRepositorio;

    @Autowired
    private NutricionistaRepositorio nutricionistaRepositorio;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CitaDTO registrar(CitaDTO citaDTO) {
        Cita cita = new Cita();

        cita.setDia(citaDTO.getDia());
        cita.setHora(citaDTO.getHora());
        cita.setDescripcion(citaDTO.getDescripcion());
        cita.setEstado(citaDTO.getEstado());
        cita.setLink(citaDTO.getLink());

        // Buscar paciente por ID
        Paciente paciente = pacienteRepositorio.findById(citaDTO.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        cita.setPaciente(paciente);

        // Validar que el paciente tenga plan premium
        if (paciente.getIdplan() == null ||
                !"Plan premium".equalsIgnoreCase(paciente.getIdplan().getTipo())) {
            throw new RuntimeException("Solo los pacientes con plan premium pueden registrar citas.");
        }
        cita.setPaciente(paciente);

        // Buscar nutricionista por ID
        Nutricionista nutricionista = nutricionistaRepositorio.findById(citaDTO.getIdNutricionista())
                .orElseThrow(() -> new RuntimeException("Nutricionista no encontrado"));
        cita.setNutricionista(nutricionista);

        // Guardar cita
        cita = citaRepositorio.save(cita);

        // Mapear a DTO
        CitaDTO dto = new CitaDTO();
        dto.setId(cita.getId());
        dto.setDia(cita.getDia());
        dto.setHora(cita.getHora());
        dto.setDescripcion(cita.getDescripcion());
        dto.setEstado(cita.getEstado());
        dto.setLink(cita.getLink());
        dto.setIdPaciente(cita.getPaciente().getId());
        dto.setIdNutricionista(cita.getNutricionista().getId());

        return dto;
    }

    @Override
    public List<CitaDTO> listarPorNutricionista(Integer idNutricionista) {
        return citaRepositorio.findByNutricionistaId(idNutricionista)
                .stream()
                .map(cita -> {
                    CitaDTO dto = new CitaDTO();
                    dto.setId(cita.getId());
                    dto.setDia(cita.getDia());
                    dto.setHora(cita.getHora());
                    dto.setDescripcion(cita.getDescripcion());
                    dto.setEstado(cita.getEstado());
                    dto.setLink(cita.getLink());
                    dto.setIdPaciente(cita.getPaciente().getId());
                    dto.setIdNutricionista(cita.getNutricionista().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CitaDTO> listarPorPaciente(Integer idPaciente) {
        return citaRepositorio.findByPacienteId(idPaciente)
                .stream()
                .map(cita -> {
                    CitaDTO dto = new CitaDTO();
                    dto.setId(cita.getId());
                    dto.setDia(cita.getDia());
                    dto.setHora(cita.getHora());
                    dto.setDescripcion(cita.getDescripcion());
                    dto.setEstado(cita.getEstado());
                    dto.setLink(cita.getLink());
                    dto.setIdPaciente(cita.getPaciente().getId());
                    dto.setIdNutricionista(cita.getNutricionista().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
