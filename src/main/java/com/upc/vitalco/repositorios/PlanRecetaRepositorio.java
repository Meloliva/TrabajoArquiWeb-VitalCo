package com.upc.vitalco.repositorios;

import com.upc.vitalco.dto.PacienteDTO;
import com.upc.vitalco.entidades.Planreceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface PlanRecetaRepositorio extends JpaRepository<Planreceta, Integer>{
    @Query("""
       SELECT pr
       FROM Planreceta pr
       WHERE pr.idplanalimenticio.idpaciente.id = :idPaciente 
         AND pr.fecharegistro = :fechRegistro
       """)
    Planreceta buscarPorPacienteYFecha(@Param("idPaciente") PacienteDTO idPaciente,
                                       @Param("fechaRegistro") LocalDate fechRegistro);

}
