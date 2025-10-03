package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.PlanRecetaReceta;
import com.upc.vitalco.entidades.Planreceta;
import com.upc.vitalco.entidades.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRecetaRecetaRepositorio extends JpaRepository<PlanRecetaReceta, Long >{
    Optional<PlanRecetaReceta> findByPlanrecetaIdAndRecetaId(Integer planrecetaId, Long recetaId);
    Optional<PlanRecetaReceta> findByPlanrecetaAndReceta(Planreceta planreceta, Receta receta);

    List<PlanRecetaReceta> findPlanRecetaRecetaByPlanreceta(Planreceta planreceta);
    List<PlanRecetaReceta> findByPlanreceta(Planreceta planreceta);

    List<PlanRecetaReceta> findByPlanrecetaId(Integer planrecetaId);
}

