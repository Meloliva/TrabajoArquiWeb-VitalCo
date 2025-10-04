package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.PlanRecetaReceta;
import com.upc.vitalco.entidades.Planreceta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRecetaRecetaRepositorio extends JpaRepository<PlanRecetaReceta, Long >{
    List<PlanRecetaReceta> findByPlanreceta(Planreceta planreceta);
}

