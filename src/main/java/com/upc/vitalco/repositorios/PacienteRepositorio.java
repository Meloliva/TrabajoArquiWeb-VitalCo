package com.upc.vitalco.repositorios;
import com.upc.vitalco.entidades.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepositorio extends JpaRepository<Paciente, Integer>{
    @Query("SELECT p FROM Paciente p WHERE p.idusuario.dni = :dni")
    Optional<Paciente> findByDni(@Param("dni") String dni);
}
