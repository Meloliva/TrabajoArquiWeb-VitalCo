package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.entidades.Seguimiento;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    // Asegúrate de que tu query sea así:
    @Modifying
    @Query("DELETE FROM Seguimiento s WHERE s.id = :seguimientoId")
    void eliminarPorId(@Param("seguimientoId") Integer seguimientoId);

    List<Seguimiento> findByPlanRecetaReceta_Planreceta_Idplanalimenticio_Id(Integer id);


}
