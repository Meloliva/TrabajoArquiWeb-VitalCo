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
    // Buscar plan activo (sin fecha fin o fecha fin futura)
    @Query("SELECT p FROM Planalimenticio p " +
            "WHERE p.idpaciente.id = :pacienteId " +
            "AND (p.fechafin IS NULL OR p.fechafin >= CURRENT_DATE) " +
            "ORDER BY p.fechainicio DESC LIMIT 1")
    Optional<Planalimenticio> buscarPlanActivo(@Param("pacienteId") Integer pacienteId);
    // Historial completo
    @Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente.id = :pacienteId ORDER BY p.fechainicio DESC")
    List<Planalimenticio> listarHistorialPorPaciente(@Param("pacienteId") Integer pacienteId);

    // ✅ Obtener el plan MÁS RECIENTE del paciente
    @Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente.id = :idPaciente ORDER BY p.fechaCreacion DESC")
    List<Planalimenticio> buscarPorPacienteOrdenado(@Param("idPaciente") Integer idPaciente);

        /**
         * ✅ Buscar todos los planes alimenticios de un paciente
         * (Devuelve TODOS, incluyendo históricos)
         */
        @Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente.id = :idPaciente ORDER BY p.fechaCreacion DESC")
        List<Planalimenticio> buscarPorPaciente(@Param("idPaciente") Integer idPaciente);

        /**
         * ✅ Buscar el plan alimenticio MÁS RECIENTE de un paciente
         * (Devuelve solo el activo actual)
         */
        @Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente.id = :idPaciente ORDER BY p.fechaCreacion DESC LIMIT 1")
        Optional<Planalimenticio> buscarPlanActivoPorPaciente(@Param("idPaciente") Integer idPaciente);

        /**
         * ✅ Buscar el plan que estaba vigente en una fecha específica
         * (Útil para historial - devuelve el plan más reciente anterior a esa fecha)
         */
        /*@Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente.id = :idPaciente " +
                "AND p.fechaCreacion <= :fecha ORDER BY p.fechaCreacion DESC LIMIT 1")
        Optional<Planalimenticio> buscarPlanEnFecha(@Param("idPaciente") Integer idPaciente,
                                                    @Param("fecha") LocalDate fecha);*/

        /**
         * ✅ Buscar planes activos (creados en los últimos X días)
         * (Útil para propagación de recetas)
         */
        @Query("SELECT p FROM Planalimenticio p WHERE p.fechaCreacion >= :fechaLimite")
        List<Planalimenticio> buscarPlanesActivos(@Param("fechaLimite") LocalDate fechaLimite);
    @Query("SELECT p FROM Planalimenticio p " +
            "WHERE p.idpaciente.id = :idPaciente " +
            "AND p.fechaCreacion <= :fecha " +
            "ORDER BY p.fechaCreacion DESC")
    List<Planalimenticio> buscarPlanesHastaFecha(@Param("idPaciente") Integer idPaciente,
                                                 @Param("fecha") LocalDate fecha);

    @Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente.id = :pacienteId " +
            "AND :fecha BETWEEN p.fechainicio AND p.fechafin")
    Optional<Planalimenticio> buscarPlanEnFecha(
            @Param("pacienteId") Integer pacienteId,
            @Param("fecha") LocalDate fecha);


}
