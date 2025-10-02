package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.PlanRecetaReceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRecetaRecetaRepositorio extends JpaRepository<PlanRecetaReceta, Long >{
    Optional<PlanRecetaReceta> findByPlanrecetaIdAndRecetaId(Integer planrecetaId, Long recetaId);
}

