package com.upc.vitalco.repositorios;
import com.upc.vitalco.dto.PacienteDTO;
import com.upc.vitalco.entidades.Planalimenticio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanAlimenticioRepositorio extends JpaRepository<Planalimenticio, Integer>{
    @Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente.id = :pacienteId ORDER BY p.fechainicio DESC LIMIT 1")
    Planalimenticio findByIdpacienteId(@Param("pacienteId") Integer pacienteId);

    @Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente.id = :pacienteId ORDER BY p.fechainicio DESC LIMIT 1")
    Planalimenticio buscarPorPaciente(@Param("pacienteId") Integer pacienteId);

    // Buscar plan activo (sin fecha fin o fecha fin futura)
    @Query("SELECT p FROM Planalimenticio p " +
            "WHERE p.idpaciente.id = :pacienteId " +
            "AND (p.fechafin IS NULL OR p.fechafin >= CURRENT_DATE) " +
            "ORDER BY p.fechainicio DESC LIMIT 1")
    Optional<Planalimenticio> buscarPlanActivo(@Param("pacienteId") Integer pacienteId);

    @Query("SELECT p FROM Planalimenticio p " +
            "WHERE p.idpaciente.id = :pacienteId " +
            "AND p.fechainicio <= :fecha " +
            "AND (p.fechafin >= :fecha OR p.fechafin IS NULL) " +
            "ORDER BY p.fechainicio DESC LIMIT 1")
    Optional<Planalimenticio> buscarPlanEnFecha(@Param("pacienteId") Integer pacienteId, @Param("fecha") LocalDate fecha);

    // Historial completo
    @Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente.id = :pacienteId ORDER BY p.fechainicio DESC")
    List<Planalimenticio> listarHistorialPorPaciente(@Param("pacienteId") Integer pacienteId);

}
