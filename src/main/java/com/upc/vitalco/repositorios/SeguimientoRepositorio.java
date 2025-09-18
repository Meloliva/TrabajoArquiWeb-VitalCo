package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.entidades.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeguimientoRepositorio extends JpaRepository<Seguimiento, Integer> {
    List<Seguimiento> findByPacienteIdAndFecha(Integer pacienteId, LocalDate fecha);
    Optional<Seguimiento> findByIdplanreceta(Planreceta idplanreceta);
}
