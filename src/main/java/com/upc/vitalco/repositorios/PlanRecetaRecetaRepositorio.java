package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.PlanRecetaReceta;
import com.upc.vitalco.entidades.Planreceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRecetaRecetaRepositorio extends JpaRepository<PlanRecetaReceta, Long >{
    Optional<PlanRecetaReceta> findByPlanrecetaIdAndRecetaId(Integer planrecetaId, Long recetaId);

    List<PlanRecetaReceta> findByPlanreceta(Planreceta planreceta);
}

