package com.upc.vitalco.services;

import com.upc.vitalco.dto.CitaDTO;
import com.upc.vitalco.entidades.Cita;
import com.upc.vitalco.entidades.Paciente;
import com.upc.vitalco.entidades.Nutricionista;
import com.upc.vitalco.interfaces.ICitaServices;
import com.upc.vitalco.repositorios.CitaRepositorio;
import com.upc.vitalco.repositorios.PacienteRepositorio;
import com.upc.vitalco.repositorios.NutricionistaRepositorio;
import com.upc.vitalco.security.util.SecurityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private SecurityUtils securityUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CitaDTO registrar(CitaDTO citaDTO) {
        // 1. VALIDACIÓN DE TIEMPO (NUEVA)
        // Comparamos la fecha y hora de la cita con el momento actual
        LocalDateTime fechaHoraCita = LocalDateTime.of(citaDTO.getDia(), citaDTO.getHora());
        if (fechaHoraCita.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No puedes registrar una cita en una fecha u hora pasada.");
        }

        Cita cita = new Cita();

        cita.setDia(citaDTO.getDia());
        cita.setHora(citaDTO.getHora());
        cita.setDescripcion(citaDTO.getDescripcion());
        cita.setLink(citaDTO.getLink());
        cita.setEstado("Pendiente");

        Paciente paciente = pacienteRepositorio.findById(citaDTO.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Validación del plan
        if (paciente.getIdplan() == null ||
                !"Plan premium".equalsIgnoreCase(paciente.getIdplan().getTipo())) {
            throw new RuntimeException("Solo los pacientes con plan premium pueden registrar citas.");
        }
        cita.setPaciente(paciente);

        Nutricionista nutricionista = nutricionistaRepositorio.findById(citaDTO.getIdNutricionista())
                .orElseThrow(() -> new RuntimeException("Nutricionista no encontrado"));

        // Validar turno del nutricionista
        if (citaDTO.getHora().isBefore(nutricionista.getIdturno().getInicioturno()) ||
                citaDTO.getHora().isAfter(nutricionista.getIdturno().getFinturno())) {
            throw new RuntimeException("La hora de la cita está fuera del turno del nutricionista.");
        }

        // Validar duplicidad
        boolean existeCita = citaRepositorio
                .findByNutricionistaIdAndDiaAndHora(nutricionista.getId(), citaDTO.getDia(), citaDTO.getHora())
                .isPresent();
        if (existeCita) {
            throw new RuntimeException("El nutricionista ya tiene una cita registrada para ese día y hora.");
        }

        cita.setNutricionista(nutricionista);
        cita = citaRepositorio.save(cita);

        // Mapeo manual de respuesta
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

    //Lista de toda la fecha
    @Override
    public List<CitaDTO> listarPorNutricionista(Integer idNutricionista, LocalDate fecha) {
        //No listar las citas canceladas
        List<Cita> citas = citaRepositorio.findByNutricionistaIdAndDia(idNutricionista, fecha);
        return citas.stream()
                .filter(cita -> !"Cancelada".equals(cita.getEstado()))
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
    //lista de hoy nutricionista
    @Override
    public List<CitaDTO> listarPorNutricionistaHoy(Integer idNutricionista) {
        return listarPorNutricionista(idNutricionista, LocalDate.now());
    }
    //lista de mañana nutricionista
    @Override
    public List<CitaDTO> listarPorNutricionistaMañana(Integer idNutricionista) {
        return listarPorNutricionista(idNutricionista, LocalDate.now().plusDays(1));
    }
    //Lista de toda la fecha
    @Override
    public List<CitaDTO> listarPorPaciente(Integer idPaciente, LocalDate fecha) {
        // Obtener todas las citas del paciente
        List<Cita> citas = citaRepositorio.findByPacienteIdAndDia(idPaciente, fecha);

        return citas.stream()
                .filter(cita -> !"Cancelada".equals(cita.getEstado()))
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
    //Lista para hoy paciente
    @Override
    public List<CitaDTO> listarPorPacienteHoy(Integer idPaciente) {
        return listarPorPaciente(idPaciente, LocalDate.now());
    }
    //Lista para mañana pacient
    @Override
    public List<CitaDTO> listarPorPacienteMañana(Integer idPaciente) {
        return listarPorPaciente(idPaciente, LocalDate.now().plusDays(1));
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
        // 1. VALIDACIÓN DE TIEMPO PARA REPROGRAMAR (NUEVA)
        LocalDateTime fechaHoraCita = LocalDateTime.of(citaDTO.getDia(), citaDTO.getHora());
        if (fechaHoraCita.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No puedes reprogramar la cita para una fecha u hora pasada.");
        }

        return citaRepositorio.findById(citaDTO.getId())
                .map(existing -> {
                    // Validar también disponibilidad del nutricionista al cambiar hora
                    // (Opcional: podrías agregar la validación de turno/disponibilidad aquí también)

                    existing.setDia(citaDTO.getDia());
                    existing.setHora(citaDTO.getHora());
                    existing.setDescripcion(citaDTO.getDescripcion());
                    existing.setLink(citaDTO.getLink());
                    // Al reprogramar, usualmente vuelve a estar Pendiente de realización
                    existing.setEstado("Pendiente");

                    // Resetear asistencias si cambias la fecha (si usas la lógica de asistencia)
                    existing.setAsistioPaciente(false);
                    existing.setAsistioNutricionista(false);

                    Cita guardado = citaRepositorio.save(existing);
                    return modelMapper.map(guardado, CitaDTO.class);
                })
                .orElseThrow(() -> new RuntimeException("Cita con ID " + citaDTO.getId() + " no encontrada"));
    }

    @Override
    public String unirseACita(Integer idCita) {
        // 1. BUSCAR LA CITA
        Cita cita = citaRepositorio.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        // 2. VALIDAR HORA (Ejemplo: Permitir entrar 10 min antes hasta 60 min después)
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaHoraCita = LocalDateTime.of(cita.getDia(), cita.getHora());

        if (ahora.isBefore(fechaHoraCita.minusMinutes(10))) {
            throw new RuntimeException("Aún es muy temprano. El enlace se habilitará 10 minutos antes de la cita.");
        }

        // Opcional: Validar si ya pasó mucho tiempo (ej. 2 horas)
        if (ahora.isAfter(fechaHoraCita.plusMinutes(5))) {
            throw new RuntimeException("La tolerancia de espera ha terminado. Ya no puedes unirte a la cita.");
        }

        // 3. IDENTIFICAR QUIÉN SE ESTÁ UNIENDO
        Integer idUsuarioLogueado = securityUtils.getUsuarioAutenticadoId();
        boolean esPaciente = cita.getPaciente().getIdusuario().getId().equals(idUsuarioLogueado);
        boolean esNutricionista = cita.getNutricionista().getIdusuario().getId().equals(idUsuarioLogueado);

        if (!esPaciente && !esNutricionista) {
            throw new RuntimeException("No tienes permiso para acceder a esta cita.");
        }

        // 4. MARCAR ASISTENCIA INDIVIDUAL (Check-in)
        if (esPaciente) {
            cita.setAsistioPaciente(true);
        } else {
            cita.setAsistioNutricionista(true);
        }

        // 5. VALIDAR SI AMBOS HAN INGRESADO
        // Solo cambiamos el estado a "Aceptada" (o "En Curso") si LOS DOS marcaron asistencia
        if (Boolean.TRUE.equals(cita.getAsistioPaciente()) && Boolean.TRUE.equals(cita.getAsistioNutricionista())) {
            if (!"Aceptada".equalsIgnoreCase(cita.getEstado())) {
                cita.setEstado("Aceptada");
            }
        }
        // Opcional: Si solo entró uno, el estado sigue "Pendiente" (o podrías poner "Esperando...")

        citaRepositorio.save(cita);

        return cita.getLink();
    }

    public boolean existeCita(Long nutricionistaId, LocalDate dia, LocalTime hora) {
        return citaRepositorio
                .findByNutricionistaIdAndDiaAndHora(nutricionistaId, dia, hora)
                .isPresent();
    }
}
