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
        cita.setLink(citaDTO.getLink());

        Paciente paciente = pacienteRepositorio.findById(citaDTO.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        cita.setPaciente(paciente);

        if (paciente.getIdplan() == null ||
                !"Plan premium".equalsIgnoreCase(paciente.getIdplan().getTipo())) {
            throw new RuntimeException("Solo los pacientes con plan premium pueden registrar citas.");
        }
        cita.setPaciente(paciente);

        Nutricionista nutricionista = nutricionistaRepositorio.findById(citaDTO.getIdNutricionista())
                .orElseThrow(() -> new RuntimeException("Nutricionista no encontrado"));
// Validar que la hora de la cita esté dentro del turno del nutricionista
        if (citaDTO.getHora().isBefore(nutricionista.getIdturno().getInicioturno()) || citaDTO.getHora().isAfter(nutricionista.getIdturno().getFinturno())) {
            throw new RuntimeException("La hora de la cita está fuera del turno del nutricionista.");
        }

// Validar que el nutricionista no tenga otra cita ese día y hora
        boolean existeCita = citaRepositorio
                .findByNutricionistaIdAndDiaAndHora(nutricionista.getId(), citaDTO.getDia(), citaDTO.getHora())
                .isPresent();
        if (existeCita) {
            throw new RuntimeException("El nutricionista ya tiene una cita registrada para ese día y hora.");
        }

        cita.setNutricionista(nutricionista);

        cita = citaRepositorio.save(cita);

        CitaDTO dto = new CitaDTO();
        dto.setId(cita.getId());
        dto.setDia(cita.getDia());
        dto.setHora(cita.getHora());
        dto.setDescripcion(cita.getDescripcion());
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
                    dto.setLink(cita.getLink());
                    dto.setIdPaciente(cita.getPaciente().getId());
                    dto.setIdNutricionista(cita.getNutricionista().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    //tiene que eliminar cita y saldria cancelada
    @Override
    public void eliminar(Integer id) {
        citaRepositorio.findById(id).ifPresent(usuario -> {
            usuario.setEstado("Cancelada");
            citaRepositorio.save(usuario);
        });
    }
    //Editar cita y saldria pendiente en estado
    @Override
    public CitaDTO actualizar(CitaDTO citaDTO) {
        return citaRepositorio.findById(citaDTO.getId())
                .map(existing -> {
                    existing.setDia(citaDTO.getDia());
                    existing.setHora(citaDTO.getHora());
                    existing.setDescripcion(citaDTO.getDescripcion());
                    existing.setLink(citaDTO.getLink());
                    existing.setEstado("Aceptada");
                    Cita guardado = citaRepositorio.save(existing);
                    return modelMapper.map(guardado, CitaDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Cita con ID " + citaDTO.getId() + " no encontrada"));
    }


}
