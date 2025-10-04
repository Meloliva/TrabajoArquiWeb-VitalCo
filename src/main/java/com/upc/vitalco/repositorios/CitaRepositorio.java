package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepositorio extends JpaRepository<Cita, Integer> {
    List<Cita> findByNutricionistaId(Integer idNutricionista);
    List<Cita> findByPacienteId(Integer idPaciente);
    boolean existsByPacienteId(Integer pacienteId);
    boolean existsByPacienteIdAndEstado(Integer pacienteId, String estado);


}
