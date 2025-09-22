package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.entidades.Seguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeguimientoRepositorio extends JpaRepository<Seguimiento, Integer> {
    @Query("SELECT s FROM Seguimiento s " +
            "WHERE s.idplanreceta.idplanalimenticio.idpaciente.id = :idPaciente " +
            "AND s.fecharegistro = :fecha")
    List<Seguimiento> buscarPorPacienteYFecha(
            @Param("idPaciente") Integer idPaciente,
            @Param("fecha") LocalDate fecha
    );
    Optional<Seguimiento> findByIdplanreceta(Planreceta idplanreceta);
    @Query("""
SELECT s FROM Seguimiento s
WHERE FUNCTION('DATE', s.fecharegistro) = :fecha
AND s.idplanreceta.idplanalimenticio.idpaciente.idusuario.username LIKE CONCAT(:inicial, '%')
""")
    List<Seguimiento> buscarPorInicialUsernameYFecha(
            @Param("inicial") String inicial,
            @Param("fecha") LocalDate fecha
    );
    @Query("""
            SELECT s FROM Seguimiento s
            JOIN s.idplanreceta pr
            JOIN pr.recetas r
            WHERE pr.idplanalimenticio.idpaciente.id = :idPaciente
            AND s.fecharegistro = :fecha
            AND r.id = :idReceta
            """)
    Optional<Seguimiento> existeRecetaEnDia(
            @Param("idPaciente") Integer idPaciente,
            @Param("fecha") LocalDate fecha,
            @Param("idReceta") Integer idReceta
    );
}
