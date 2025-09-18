package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Planreceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PlanRecetaRepositorio extends JpaRepository<Planreceta, Integer>{
    Planreceta findByIdplanalimenticio_Idpaciente_IdAndFecha(Integer pacienteId, LocalDate fecha);
}
