package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Plansuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanSuscripcionRepositorio extends JpaRepository<Plansuscripcion, Integer> {

}
