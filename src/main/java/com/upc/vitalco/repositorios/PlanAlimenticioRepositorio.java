package com.upc.vitalco.repositorios;
import com.upc.vitalco.dto.PacienteDTO;
import com.upc.vitalco.entidades.Planalimenticio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanAlimenticioRepositorio extends JpaRepository<Planalimenticio, Integer>{
    @Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente = :pacienteId")
    Planalimenticio buscarPorPacienteId(PacienteDTO pacienteId);


    Planalimenticio findByIdpacienteId(Integer pacienteId);

    @Query("SELECT p FROM Planalimenticio p WHERE p.idpaciente.id = :pacienteId")
    Planalimenticio buscarPorPaciente(@Param("pacienteId") Integer pacienteId);

}
