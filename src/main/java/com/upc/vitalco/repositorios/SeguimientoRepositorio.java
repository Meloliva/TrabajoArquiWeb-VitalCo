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
            "WHERE s.planRecetaReceta.planreceta.idplanalimenticio.idpaciente.id = :idPaciente " +
            "AND s.fecharegistro = :fecha")
    List<Seguimiento> buscarPorPacienteYFecha(
            @Param("idPaciente") Integer idPaciente,
            @Param("fecha") LocalDate fecha
    );
    @Query("""
SELECT s FROM Seguimiento s
WHERE FUNCTION('DATE', s.fecharegistro) = :fecha
AND s.planRecetaReceta.planreceta.idplanalimenticio.idpaciente.idusuario.dni LIKE CONCAT(:dni, '%')
""")
    List<Seguimiento> buscarPorInicialUsernameYFecha(
            @Param("dni") String dni,
            @Param("fecha") LocalDate fecha
    );
    /*@Query("""
            SELECT s FROM Seguimiento s
            JOIN s.planRecetaReceta. pr
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
    @Query("SELECT s FROM Seguimiento s WHERE s.idplanreceta.id = :idPlanReceta AND s.fecharegistro = :fechaRegistro")
    Optional<Seguimiento> buscarPorPlanRecetaYFecha(Integer idPlanReceta, LocalDate fechaRegistro);*/

    @Query("""
       SELECT s FROM Seguimiento s
       JOIN s.planRecetaReceta prr
       JOIN prr.planreceta pr
       JOIN pr.idplanalimenticio pa
       JOIN pa.idpaciente p
       JOIN p.idusuario u
       WHERE u.dni = :dni AND s.fecharegistro = :fecha
       """)
    List<Seguimiento> buscarPorDniYFecha(@Param("dni") String dni,
                                         @Param("fecha") LocalDate fecha);

}
